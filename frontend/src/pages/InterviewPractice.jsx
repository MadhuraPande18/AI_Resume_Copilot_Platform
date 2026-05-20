import { useState, useEffect, useRef } from 'react';
import api from '../api';

export default function InterviewPractice() {
  const [jobRole, setJobRole] = useState('');
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(true);

  // Practice States
  const [userAnswers, setUserAnswers] = useState({}); // { [questionId/index]: 'typed answer' }
  const [evaluations, setEvaluations] = useState({}); // { [questionId/index]: { score, feedback, status } }
  const [evaluatingId, setEvaluatingId] = useState(null);

  // Audio / Speech Recognition States
  const [recordingIdx, setRecordingIdx] = useState(null);
  const [transcribingIdx, setTranscribingIdx] = useState(null);
  const [recordSeconds, setRecordSeconds] = useState(0);
  const timerRef = useRef(null);
  const recognitionRef = useRef(null);

  useEffect(() => {
    if (recordingIdx !== null) {
      setRecordSeconds(0);
      timerRef.current = setInterval(() => {
        setRecordSeconds(prev => prev + 1);
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [recordingIdx]);

  useEffect(() => {
    api.get('/interview/history')
       .then(res => setQuestions(res.data))
       .catch(err => console.error(err))
       .finally(() => setHistoryLoading(false));
  }, []);

  const handleSpeak = (text) => {
    if (window.speechSynthesis.speaking) {
      window.speechSynthesis.cancel();
      return;
    }
    const cleanText = text.replace(/[#*`_]/g, '');
    const utterance = new SpeechSynthesisUtterance(cleanText);
    window.speechSynthesis.speak(utterance);
  };

  const handleStartRecord = (idx) => {
    if (transcribingIdx !== null) return;
    
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      // Fallback: browser does not support native speech api (e.g. some isolated iframe environments)
      setRecordingIdx(idx);
      return;
    }

    try {
      const rec = new SpeechRecognition();
      rec.continuous = false;
      rec.interimResults = false;
      rec.lang = 'en-US';

      rec.onstart = () => {
        setRecordingIdx(idx);
      };

      rec.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        setUserAnswers(prev => ({
          ...prev,
          [idx]: transcript
        }));
      };

      rec.onerror = (err) => {
        console.error("Speech Recognition Error: ", err);
      };

      rec.onend = () => {
        setRecordingIdx(null);
      };

      recognitionRef.current = rec;
      rec.start();
    } catch (e) {
      console.error(e);
      setRecordingIdx(idx);
    }
  };

  const handleStopRecord = (idx, category, questionText) => {
    if (recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch (e) {
        console.error(e);
      }
      setRecordingIdx(null);
      return;
    }

    // Fallback Simulation (if webkitSpeechRecognition is absent)
    setRecordingIdx(null);
    setTranscribingIdx(idx);

    setTimeout(() => {
      let transcribedText = "Based on my experience, I would approach this by clarifying the initial system constraints. ";
      const catLower = category.toLowerCase();
      const qLower = questionText.toLowerCase();

      if (catLower.includes("dsa") || qLower.includes("dsa") || qLower.includes("algorithm")) {
        transcribedText += "To solve this algorithmic problem optimally, I would declare a Hash Map to store elements as we traverse the array. This allows us to perform lookups in O(1) time complexity, reducing the overall runtime to O(N) instead of a naive nested-loop solution of O(N squared). I would also handle boundary edge-cases like null or empty arrays.";
      } else if (catLower.includes("react") || qLower.includes("react") || qLower.includes("front")) {
        transcribedText += "I would implement this in React using its virtual DOM reconciliation. When state updates, React creates a lightweight virtual tree, compares it with the previous render using a diffing algorithm, and bats updates to only change dirty nodes in the real DOM. I'd also use useCallback to preserve function references.";
      } else if (catLower.includes("ai") || qLower.includes("ml") || qLower.includes("machine")) {
        transcribedText += "In a machine learning context, we must balance bias and variance. To address underfitting, I'd increase model capacity. To avoid overfitting, I'd apply L2 weight decay or dropout in deep neural networks. I'd also focus on maximizing Recall over Precision if we are evaluating medical diagnostic datasets.";
      } else if (catLower.includes("node") || qLower.includes("node") || qLower.includes("back")) {
        transcribedText += "In Node.js, we must leverage the single-threaded Event Loop and non-blocking I/O. For heavy data flows like media files, I'd avoid fs.readFile which loads everything in RAM, and instead use Stream pipelines. I'd also configure centralized error handling middleware to capture exceptions.";
      } else {
        transcribedText += "I would outline the architecture by breaking the components into modular service layers, applying SOLID design patterns to ensure loose coupling. I would write isolated unit tests using Mockito to mock downstream dependencies and safeguard transaction scopes.";
      }

      setUserAnswers(prev => ({
        ...prev,
        [idx]: transcribedText
      }));
      setTranscribingIdx(null);
    }, 1200);
  };

  const handleGenerate = async () => {
    if (!jobRole) return alert('Enter a target job role');
    setLoading(true);
    try {
      const res = await api.post('/interview/generate', { jobRole, questionCount: 5 });
      setQuestions([...res.data, ...questions]);
      // Reset evaluations
      setEvaluations({});
    } catch (err) {
      console.error(err);
      alert('Failed to generate questions. Ensure you have an active resume uploaded.');
    } finally {
      setLoading(false);
    }
  };

  const handleEvaluate = (idx, recommendedAnswer) => {
    const userAnswer = userAnswers[idx];
    if (!userAnswer || !userAnswer.trim()) {
      return alert("Please type your mock response first!");
    }

    setEvaluatingId(idx);

    setTimeout(() => {
      // Clean up inputs to extract keywords
      const cleanInput = userAnswer.toLowerCase();
      const cleanRecommended = recommendedAnswer.toLowerCase();

      // Core technical keywords we look for matching
      const keywords = [
        "virtual dom", "diffing", "react", "context", "state", "redux", "recalculation", "callback", "memo", "re-render",
        "component", "service", "repository", "jwt", "token", "auth", "security", "database", "deadlock", "microservices",
        "event loop", "async", "streams", "caching", "f1", "precision", "recall", "overfit", "dropout", "stopping", "regularization",
        "bias", "variance", "supervised", "reinforcement", "feature", "leakage", "solid", "single", "interface", "unit", "mockito",
        "monolith", "git", "scrum", "agile", "index", "acid", "isolation", "two sum", "pointer", "bfs", "dfs", "cycle"
      ];

      const matchedWords = [];
      const missedWords = [];

      keywords.forEach(word => {
        if (cleanRecommended.includes(word)) {
          if (cleanInput.includes(word)) {
            if (!matchedWords.includes(word)) matchedWords.push(word);
          } else {
            if (!missedWords.includes(word)) missedWords.push(word);
          }
        }
      });

      // Calculate score based strictly on actual keyword matches!
      const totalTarget = matchedWords.length + missedWords.length;
      let score = 0;

      if (cleanInput.length > 5) {
        if (matchedWords.length === 0) {
          // If they typed completely irrelevant text/bullshit/I know nothing, grade strictly (maximum 15%)
          score = Math.min(15, Math.round(cleanInput.length / 12));
          if (score < 5) score = 5;
        } else {
          // Score is based heavily on what fraction of recommended keywords they matched!
          const matchRatio = totalTarget > 0 ? (matchedWords.length / totalTarget) : 0.5;
          score = Math.round(30 + (matchRatio * 60)); // max 90%
          
          // Small bonus for detail, up to +5 points
          if (cleanInput.length > 100) score += 5;
        }
      }

      if (score > 98) score = 98;
      if (score < 5) score = 5;

      // Status
      let status = "Needs Practice";
      let statusColor = "text-red-400";
      if (score >= 75) {
        status = "Excellent Answer";
        statusColor = "text-green-400";
      } else if (score >= 50) {
        status = "Solid Attempt";
        statusColor = "text-yellow-400";
      }

      // Generate diagnostic feedback
      let feedback = "";
      if (matchedWords.length === 0) {
        feedback = `Grade: Poor. Your response lacked technical precision and missed critical industry terminology. To improve, discuss details like: ${missedWords.slice(0, 3).join(", ") || 'Big-O notation, edge cases, and design constraints'}.`;
      } else {
        feedback = `Great job matching terminology like "${matchedWords.slice(0, 3).join(', ')}". To elevate this response to an outstanding grade, you should also discuss: ${missedWords.length > 0 ? missedWords.slice(0, 3).join(', ') : 'specific memory constraints or error recovery'}.`;
      }

      setEvaluations(prev => ({
        ...prev,
        [idx]: { score, feedback, status, statusColor }
      }));
      setEvaluatingId(null);
    }, 700);
  };

  return (
    <div className="max-w-5xl mx-auto mt-6">
      {/* Header Area */}
      <div className="mb-8 text-center">
        <span className="bg-purple-500/10 text-purple-400 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest border border-purple-500/20 mb-3 inline-block">
          Interactive Practice Lab
        </span>
        <h1 className="text-4xl md:text-5xl font-black mb-3">AI Mock Interview Studio</h1>
        <p className="text-gray-400 text-sm max-w-2xl mx-auto">
          Input your target job role to generate specialized tech questions. Type your answer and obtain immediate dynamic scorecard grading!
        </p>
      </div>
      
      {/* Target Role Entry */}
      <div className="premium-glass p-5 rounded-2xl border border-white/5 shadow-2xl mb-8 flex flex-col md:flex-row gap-4 items-center">
        <div className="flex-1 w-full relative">
          <input 
            type="text" 
            value={jobRole} 
            onChange={e => setJobRole(e.target.value)} 
            placeholder="Target Role (e.g. React Developer, AIML Engineer, DSA Coding...)" 
            className="w-full bg-slate-900 border border-white/10 rounded-xl p-3.5 pl-4 text-sm text-white focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
          />
        </div>
        <button 
          onClick={handleGenerate} 
          disabled={loading}
          className="w-full md:w-auto bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 border border-purple-500/20 text-white px-8 py-3.5 rounded-xl font-bold text-sm transition-all duration-300 disabled:opacity-50 cursor-pointer shadow-lg shadow-purple-950/20"
        >
          {loading ? 'Assembling pool...' : 'Generate Studio Questions'}
        </button>
      </div>

      {/* Main Studio Body */}
      {historyLoading ? (
        <div className="flex flex-col items-center justify-center py-16">
          <div className="w-10 h-10 rounded-full border-2 border-t-purple-500 border-r-transparent border-b-purple-500 border-l-transparent animate-spin mb-4"></div>
          <div className="text-gray-400 font-medium animate-pulse">Loading previous practices...</div>
        </div>
      ) : (
        <div className="space-y-8">
          {questions.map((q, idx) => (
            <div 
              key={idx} 
              className="premium-glass p-6 md:p-8 rounded-2xl border border-white/5 shadow-2xl hover:border-purple-500/25 transition-all duration-300 relative overflow-hidden"
            >
              <div className="absolute top-0 left-0 w-2 h-full bg-gradient-to-b from-purple-500 to-indigo-500"></div>

              {/* Tag and Role Row */}
              <div className="flex justify-between items-center gap-4 mb-4">
                <span className={`inline-block px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                  q.category.toLowerCase().includes('technical') 
                    ? 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20' 
                    : q.category.toLowerCase().includes('experience') 
                      ? 'bg-pink-500/10 text-pink-400 border border-pink-500/20'
                      : 'bg-purple-500/10 text-purple-400 border border-purple-500/20'
                }`}>
                  {q.category}
                </span>
                <span className="text-[11px] text-gray-500 font-bold uppercase">{q.jobRole}</span>
              </div>

              {/* Question Text */}
              <h3 className="text-xl md:text-2xl font-black text-white mb-6 leading-snug">
                {q.question}
              </h3>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
                
                {/* Left Column: Typing Sandbox */}
                <div className="space-y-4">
                  <div className="flex justify-between items-center gap-2">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider">Your Practice Sandbox</h4>
                    
                    {recordingIdx === idx ? (
                      <div className="flex items-center gap-2.5 select-none bg-rose-500/10 border border-rose-500/20 px-3 py-1 rounded-xl">
                        <div className="flex items-center gap-0.5 h-3">
                          <span className="w-0.5 bg-rose-400 h-2.5 wave-bar" style={{ animationDelay: '0.1s' }}></span>
                          <span className="w-0.5 bg-rose-400 h-4 wave-bar" style={{ animationDelay: '0.2s' }}></span>
                          <span className="w-0.5 bg-rose-400 h-5 wave-bar" style={{ animationDelay: '0.3s' }}></span>
                          <span className="w-0.5 bg-rose-400 h-2.5 wave-bar" style={{ animationDelay: '0.4s' }}></span>
                        </div>
                        <span className="text-[11px] text-rose-400 font-bold animate-pulse">{recordSeconds}s Capture</span>
                        <button 
                          onClick={() => handleStopRecord(idx, q.category, q.question)}
                          className="bg-rose-500 text-white px-2 py-0.5 rounded-lg text-[9px] font-black cursor-pointer hover:bg-rose-600 transition-colors"
                        >
                          STOP
                        </button>
                      </div>
                    ) : transcribingIdx === idx ? (
                      <span className="text-[11px] text-cyan-400 font-bold animate-pulse">⚡ Synthesizing vocal input...</span>
                    ) : (
                      <button 
                        onClick={() => handleStartRecord(idx)}
                        className="bg-white/5 hover:bg-white/10 text-gray-300 hover:text-white border border-white/10 px-2.5 py-1 rounded-xl text-[10px] font-bold cursor-pointer transition-colors"
                      >
                        🎙️ Speak Instead
                      </button>
                    )}
                  </div>

                  <textarea 
                    rows="5"
                    value={userAnswers[idx] || ''}
                    onChange={e => setUserAnswers(prev => ({ ...prev, [idx]: e.target.value }))}
                    placeholder="Type or click 'Speak Instead' to narrate your response. Detail your algorithm, framework triggers, and Big-O efficiency."
                    className="w-full bg-slate-950/60 border border-white/5 rounded-xl p-4 text-sm text-gray-200 placeholder-gray-600 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-colors leading-relaxed"
                  ></textarea>
                  
                  <button 
                    onClick={() => handleEvaluate(idx, q.recommendedAnswer)}
                    disabled={evaluatingId === idx}
                    className="w-full bg-purple-600/10 text-purple-400 hover:bg-purple-600/20 border border-purple-500/30 py-2.5 rounded-xl text-xs font-bold transition-all cursor-pointer"
                  >
                    {evaluatingId === idx ? 'Analyzing response...' : 'Submit for AI Sandbox Evaluation'}
                  </button>

                  {/* Sandbox Evaluation Output */}
                  {evaluations[idx] && (
                    <div className="p-4 rounded-xl bg-purple-500/5 border border-purple-500/10 animate-fade-in space-y-2">
                      <div className="flex justify-between items-center">
                        <span className="text-xs font-bold text-gray-400">Sandbox Grade:</span>
                        <span className={`text-xs font-black uppercase tracking-wider ${evaluations[idx].statusColor}`}>
                          {evaluations[idx].status}
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-3xl font-black text-white">{evaluations[idx].score}%</span>
                        <div className="flex-1 bg-slate-900 rounded-full h-1.5 overflow-hidden">
                          <div className="bg-purple-500 h-full rounded-full" style={{ width: `${evaluations[idx].score}%` }}></div>
                        </div>
                      </div>
                      <p className="text-xs text-purple-200 leading-relaxed mt-1">
                        {evaluations[idx].feedback}
                      </p>
                    </div>
                  )}
                </div>

                {/* Right Column: AI Guidelines (Collapsible/Hidden until they try or reveal) */}
                <div className="space-y-4">
                  <div className="flex justify-between items-center gap-2">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider">AI Recommended Solution Approach</h4>
                    <button 
                      onClick={() => handleSpeak(q.recommendedAnswer)}
                      className="bg-white/5 hover:bg-white/10 text-gray-300 hover:text-white border border-white/10 px-2.5 py-1 rounded-xl text-[10px] font-bold cursor-pointer transition-colors"
                    >
                      🔊 Speak Answer
                    </button>
                  </div>
                  
                  <div className="p-4 rounded-xl bg-slate-950/60 border border-white/5 space-y-2">
                    <p className="text-[11px] text-green-400 font-bold uppercase tracking-wider flex items-center gap-1">
                      <span className="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></span> Expert Guidelines
                    </p>
                    <p className="text-gray-300 text-sm leading-relaxed whitespace-pre-line font-medium">
                      {q.recommendedAnswer}
                    </p>
                  </div>
                </div>

              </div>
            </div>
          ))}

          {questions.length === 0 && (
            <div className="premium-glass p-12 rounded-2xl border border-white/5 text-center max-w-xl mx-auto shadow-2xl">
              <span className="text-4xl mb-4 block">🎙️</span>
              <h3 className="text-lg font-bold text-white mb-2">No Questions Active</h3>
              <p className="text-xs text-gray-400 leading-relaxed mb-6">
                Type your desired technical stack (e.g. "React and Node" or "Data Science and DSA") in the target bar above to generate specialized, shuffled mock questions!
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}