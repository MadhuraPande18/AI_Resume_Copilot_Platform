import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';

export default function Dashboard() {
  const [stats, setStats] = useState({ totalResumes: 0, averageAtsScore: 0, totalQuestionsGenerated: 0 });
  const [loading, setLoading] = useState(true);
  
  // Interactive Checklist State
  const [checklist, setChecklist] = useState([
    { id: 1, text: "Upload dynamic resume PDF", completed: false },
    { id: 2, text: "Run ATS Scanner against a Target Job Description", completed: false },
    { id: 3, text: "Review matched vs missing technical keywords", completed: false },
    { id: 4, text: "Generate custom tailored AI questions (React/Node/AIML/DSA)", completed: false },
    { id: 5, text: "Practice mock answering and read recommended guidelines", completed: false },
    { id: 6, text: "Explore AI Chatbot Career Roadmaps", completed: false }
  ]);

  const toggleCheck = (id) => {
    setChecklist(prev => prev.map(item => item.id === id ? { ...item, completed: !item.completed } : item));
  };

  const completedCount = checklist.filter(item => item.completed).length;
  const progressPercent = Math.round((completedCount / checklist.length) * 100);

  // Parse email from JWT
  let candidateEmail = "Candidate";
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      candidateEmail = payload.sub || "Candidate";
    } catch(e) {}
  }

  useEffect(() => {
    api.get('/dashboard/analytics').then(res => {
      setStats(res.data);
      setLoading(false);
    }).catch(err => {
      console.error(err);
      setLoading(false);
    });
  }, []);

  if (loading) return (
    <div className="flex flex-col items-center justify-center min-h-[50vh]">
      <div className="w-12 h-12 rounded-full border-4 border-t-purple-500 border-r-transparent border-b-purple-500 border-l-transparent animate-spin mb-4"></div>
      <div className="text-gray-400 font-medium animate-pulse">Assembling dynamic cockpit analytics...</div>
    </div>
  );

  return (
    <div className="max-w-5xl mx-auto mt-6">
      {/* Header Area */}
      <div className="mb-10 text-center md:text-left">
        <span className="bg-purple-500/10 text-purple-400 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest border border-purple-500/20 mb-3 inline-block">
          AI Interview Cockpit
        </span>
        <h1 className="text-4xl md:text-5xl font-black mb-3">
          Welcome back, <span className="glow-text-premium">{candidateEmail}</span>!
        </h1>
        <p className="text-gray-400 text-base max-w-2xl leading-relaxed">
          Monitor your resume's competitive standing, simulate dynamic technical interview sessions, and track your active progress checklist to landing your dream offer.
        </p>
      </div>
      
      {/* Key Metric Glass Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        <div className="premium-glass p-6 rounded-2xl border border-white/5 shadow-2xl relative overflow-hidden group hover:border-cyan-500/30 transition-all duration-300 transform hover:-translate-y-1">
          <div className="absolute top-0 right-0 w-24 h-24 bg-cyan-500/5 rounded-full blur-2xl group-hover:bg-cyan-500/10 transition-colors"></div>
          <h3 className="text-gray-400 font-bold text-xs uppercase tracking-wider mb-2">Resumes Tracked</h3>
          <p className="text-5xl font-black text-cyan-400">{stats.totalResumes}</p>
          <span className="text-[11px] text-gray-500 mt-2 block">Active document automatically loaded</span>
        </div>
        
        <div className="premium-glass p-6 rounded-2xl border border-white/5 shadow-2xl relative overflow-hidden group hover:border-green-500/30 transition-all duration-300 transform hover:-translate-y-1">
          <div className="absolute top-0 right-0 w-24 h-24 bg-green-500/5 rounded-full blur-2xl group-hover:bg-green-500/10 transition-colors"></div>
          <h3 className="text-gray-400 font-bold text-xs uppercase tracking-wider mb-2">Average ATS Score</h3>
          <div className="flex items-center gap-3">
            <p className="text-5xl font-black text-green-400">{stats.averageAtsScore}%</p>
            <div className="w-2.5 h-2.5 rounded-full bg-green-500 animate-pulse"></div>
          </div>
          <span className="text-[11px] text-gray-500 mt-2 block">Targeting standard commercial parsers</span>
        </div>
        
        <div className="premium-glass p-6 rounded-2xl border border-white/5 shadow-2xl relative overflow-hidden group hover:border-purple-500/30 transition-all duration-300 transform hover:-translate-y-1">
          <div className="absolute top-0 right-0 w-24 h-24 bg-purple-500/5 rounded-full blur-2xl group-hover:bg-purple-500/10 transition-colors"></div>
          <h3 className="text-gray-400 font-bold text-xs uppercase tracking-wider mb-2">Mock Questions Tracked</h3>
          <p className="text-5xl font-black text-purple-400">{stats.totalQuestionsGenerated}</p>
          <span className="text-[11px] text-gray-500 mt-2 block">Tailored for Frontend, Backend, AI & DSA</span>
        </div>
      </div>

      {/* Main Grid: Core Actions & Interactive Preparation Checklist */}
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8">
        
        {/* Core Actions Column */}
        <div className="lg:col-span-2 space-y-6">
          <h2 className="text-xl font-extrabold text-white mb-2 flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-sm bg-purple-500"></span> Launch Core Engines
          </h2>
          
          <Link to="/upload" className="block premium-glass p-6 rounded-2xl border border-white/5 hover:border-cyan-500/40 hover:shadow-cyan-500/5 transition-all duration-300 transform hover:-translate-y-1 group">
            <div className="flex items-center gap-4 mb-3">
              <div className="w-12 h-12 rounded-xl bg-cyan-500/10 flex items-center justify-center text-cyan-400 font-bold group-hover:bg-cyan-500/20 transition-all">
                🗂️
              </div>
              <div>
                <h3 className="text-lg font-black text-white group-hover:text-cyan-400 transition-colors">Resume Optimizer</h3>
                <p className="text-xs text-cyan-400/80">Offline ATS Engine Activated</p>
              </div>
            </div>
            <p className="text-xs text-gray-400 leading-relaxed">
              Upload your resume and test it against various job descriptions. Instantly match skills and obtain specific dynamic feedback to fix keywords.
            </p>
          </Link>
          
          <Link to="/practice" className="block premium-glass p-6 rounded-2xl border border-white/5 hover:border-purple-500/40 hover:shadow-purple-500/5 transition-all duration-300 transform hover:-translate-y-1 group">
            <div className="flex items-center gap-4 mb-3">
              <div className="w-12 h-12 rounded-xl bg-purple-500/10 flex items-center justify-center text-purple-400 font-bold group-hover:bg-purple-500/20 transition-all">
                🎙️
              </div>
              <div>
                <h3 className="text-lg font-black text-white group-hover:text-purple-400 transition-colors">Mock Studio</h3>
                <p className="text-xs text-purple-400/80">Question Shuffler Ready</p>
              </div>
            </div>
            <p className="text-xs text-gray-400 leading-relaxed">
              Generate custom structured questions for DSA, AIML, React, Node, or Fullstack. Read expert answers and type custom answers for immediate evaluations.
            </p>
          </Link>
        </div>

        {/* Preparation Checklist Scorecard */}
        <div className="lg:col-span-3 premium-glass p-6 rounded-2xl border border-white/5 flex flex-col shadow-2xl">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 border-b border-white/5 pb-4 mb-5">
            <div>
              <h2 className="text-xl font-black text-white">Interview Readiness Checklist</h2>
              <p className="text-xs text-gray-400 mt-0.5">Track your preparation step-by-step</p>
            </div>
            {/* Dynamic Progress indicator */}
            <div className="flex items-center gap-3">
              <div className="text-right">
                <span className="text-xs text-gray-400 font-bold uppercase tracking-wider block">Completeness</span>
                <span className="text-lg font-black text-white">{progressPercent}%</span>
              </div>
              <div className="relative w-12 h-12 flex items-center justify-center bg-purple-500/10 border border-purple-500/20 rounded-full font-black text-xs text-purple-300">
                {completedCount}/{checklist.length}
              </div>
            </div>
          </div>

          {/* Dynamic glowing progress bar */}
          <div className="w-full bg-slate-900 rounded-full h-2 mb-6 overflow-hidden border border-white/5">
            <div 
              className="bg-gradient-to-r from-cyan-400 via-purple-500 to-pink-500 h-full rounded-full transition-all duration-500 ease-out shadow-lg shadow-purple-500/35"
              style={{ width: `${progressPercent}%` }}
            ></div>
          </div>

          {/* Checklist options */}
          <div className="flex-1 space-y-3.5 select-none">
            {checklist.map((item) => (
              <div 
                key={item.id} 
                onClick={() => toggleCheck(item.id)}
                className={`p-3 rounded-xl border flex items-center gap-3.5 cursor-pointer transition-all duration-200 ${
                  item.completed 
                    ? 'bg-purple-600/10 border-purple-500/30 text-purple-200' 
                    : 'bg-slate-900/40 border-white/5 text-gray-400 hover:bg-slate-900/60 hover:text-white'
                }`}
              >
                <div className={`w-5 h-5 rounded-lg flex items-center justify-center border transition-all ${
                  item.completed 
                    ? 'bg-purple-500 border-purple-400 text-white' 
                    : 'border-gray-600 bg-transparent'
                }`}>
                  {item.completed && (
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                    </svg>
                  )}
                </div>
                <span className={`text-xs font-semibold leading-relaxed ${item.completed ? 'line-through opacity-70' : ''}`}>
                  {item.text}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}