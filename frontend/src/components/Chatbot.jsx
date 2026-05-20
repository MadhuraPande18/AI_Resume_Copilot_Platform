import { useState, useRef, useEffect } from 'react';

export default function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { sender: 'bot', text: "👋 Welcome! I am your AI Career Copilot. I'm here to help you ace your interviews!\n\nChoose one of the specialized cheat sheets below or ask me any question directly!" }
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const chatEndRef = useRef(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  const presetResponses = {
    dsa: {
      title: "📘 Data Structures & Algorithms Cheat Sheet",
      content: `Here are the top 4 patterns to master for technical coding interviews:

1. **Sliding Window:** Optimal for subarray/substring searches (e.g. Longest Substring Without Repeating Characters). Time: O(N).
2. **Two Pointers:** Perfect for sorted arrays/lists (e.g. Two Sum, Container With Most Water, reversing arrays). Time: O(N), Space: O(1).
3. **Fast & Slow Pointers:** Ideal for linked list cycle detection, middle node retrieval, or finding loops. Time: O(N), Space: O(1).
4. **BFS & DFS Traversal:** 
   - **BFS:** Shortest path in unweighted graphs. Uses a queue.
   - **DFS:** Connectivity, pathfinding, maze solving. Uses recursion or stack.

*💡 Pro-tip: Always clarify input constraints (e.g. sorted vs unsorted, negatives allowed) and declare Big-O time and space complexity immediately.*`
    },
    aiml: {
      title: "🧠 AI/ML & Data Science Roadmap",
      content: `To crack machine learning engineering interviews, master these core pillars:

1. **Model Evaluation:** Understand Precision vs. Recall (Precision avoids false positives, Recall avoids false negatives) and the F1-score. Know ROC-AUC curves.
2. **Avoid Overfitting:** Be ready to explain regularization (L1/L2), early stopping, and Dropout in deep learning.
3. **Data Quality:** Master handling class imbalance (SMOTE, downsampling, custom loss weights) and feature normalization.
4. **LLMs & Transformers:** Learn self-attention mechanism, positional encoding, fine-tuning techniques (LoRA, QLoRA), and RAG (Retrieval-Augmented Generation).

*💡 Interview Tip: When asked about a project, always discuss feature engineering decisions and how you validated the model on test data.*`
    },
    react: {
      title: "⚡ React & Modern Frontend Checklist",
      content: `Master these high-frequency React & frontend topics:

1. **Virtual DOM:** React's lightweight in-memory representation. State change -> Reconciliation (diffing algorithm) -> updates only dirty nodes in the real DOM.
2. **Hooks Optimization:**
   - \`useMemo\`: Memoizes calculated values to avoid expensive loops on re-render.
   - \`useCallback\`: Memoizes function references to prevent child components from re-rendering.
3. **State Management:** Local state for component-specific UI, Context API for read-only global tokens (theme, lang), and Zustand/Redux for high-frequency shared states.
4. **Core JavaScript:** Closures (encapsulating state), Event Loop (microtasks vs macrotasks), Promises, and strict type-safety in TypeScript.`
    },
    star: {
      title: "💡 Behavioral Prep: The STAR Method",
      content: `Use the **STAR** framework to structure every behavioral question:

* **S - Situation:** Set the scene. Give brief context (e.g., "In my final year fullstack team project, we had a critical database deadlock 2 days before the demo...").
* **T - Task:** State the objective and what needed to be done (e.g., "My task was to optimize the SQL queries and database transaction pool...").
* **A - Action:** Detail *your* specific technical or communicative steps (e.g., "I isolated queries, indexed the foreign keys, set version locks, and set connection limits...").
* **R - Result:** Share the quantitative positive outcome (e.g., "We fixed the bug, achieved a 4x faster load speed, and passed our review with an A grade.").`
    },
    fullstack: {
      title: "🏗️ System Design & Databases Cheat Sheet",
      content: `Key patterns for Fullstack and System Design interviews:

1. **Database Indexing:** B-Tree index keeps search operations logarithmic O(log N) instead of linear scan O(N). Avoid overindexing as it slows down inserts/updates.
2. **Stateless JWT Auth:** User logs in -> Server issues short-lived JWT (access) in memory and refresh token in HttpOnly secure Cookie. Stateless verify.
3. **Caching Strategy:** Cache aside pattern using Redis for high-read databases. Implement proper cache eviction policies and Time-To-Live (TTL).
4. **ACID Properties:** Atomicity (all-or-nothing), Consistency (state-validity), Isolation (independent transactions), Durability (persistence).`
    }
  };

  const handleSend = (textToSend) => {
    const text = textToSend || inputValue;
    if (!text.trim()) return;

    // Add user message
    const newUserMessage = { sender: 'user', text };
    setMessages(prev => [...prev, newUserMessage]);
    setInputValue('');
    setIsTyping(true);

    // Dynamic response logic
    setTimeout(() => {
      let reply = "";
      const lower = text.toLowerCase().trim();

      if (lower === "hello" || lower === "hi" || lower === "hey" || lower === "yo" || lower === "greetings") {
        reply = `### 👋 Hello! Welcome!
        
I am your AI Career Copilot advisor. I'm here to help you prepare for technical, architectural, and behavioral evaluations!

Here is how you can utilize my workbench:
1. Ask specific technical topics (e.g. *"What is the event loop?"*, *"How does virtual DOM work?"*).
2. Grab targeted cheat sheets using the quick shortcuts below.
3. Learn structured behavioral templates using the **STAR Method** button.

What topic are we mastering today?`;
      } else if (lower.includes("dsa") || lower.includes("algorithm") || lower.includes("leetcode") || lower.includes("sort") || lower.includes("array") || lower.includes("tree")) {
        reply = `### ${presetResponses.dsa.title}\n\n${presetResponses.dsa.content}`;
      } else if (lower.includes("ml") || lower.includes("ai") || lower.includes("machine") || lower.includes("data science") || lower.includes("deep") || lower.includes("nlp") || lower.includes("neural") || lower.includes("model")) {
        reply = `### ${presetResponses.aiml.title}\n\n${presetResponses.aiml.content}`;
      } else if (lower.includes("react") || lower.includes("front") || lower.includes("hook") || lower.includes("js") || lower.includes("html") || lower.includes("css") || lower.includes("web") || lower.includes("dom") || lower.includes("closure")) {
        reply = `### ${presetResponses.react.title}\n\n${presetResponses.react.content}`;
      } else if (lower.includes("star") || lower.includes("behavior") || lower.includes("disagree") || lower.includes("conflict") || lower.includes("situat") || lower.includes("question")) {
        reply = `### ${presetResponses.star.title}\n\n${presetResponses.star.content}`;
      } else if (lower.includes("full") || lower.includes("db") || lower.includes("sql") || lower.includes("node") || lower.includes("system") || lower.includes("index") || lower.includes("backend") || lower.includes("postgres")) {
        reply = `### ${presetResponses.fullstack.title}\n\n${presetResponses.fullstack.content}`;
      } else {
        reply = `### 🌟 Career Coaching Insight\n\nThat's a useful question! In technical coding interviews, remember that **communication is 50% of the grade**. \n\nWhen tackling technical challenges:\n1. **Talk out loud** as you code or design.\n2. **State assumptions** before diving into implementation.\n3. **Discuss edge cases** (null inputs, empty values, boundaries).\n4. **Walk through the Big-O complexity** without being prompted.\n\nWould you like me to share a specific cheat sheet for **DSA**, **AI/ML**, **React/Frontend**, **Fullstack/Node**, or the **STAR method**?`;
      }

      setMessages(prev => [...prev, { sender: 'bot', text: reply }]);
      setIsTyping(false);
    }, 800);
  };

  const selectPreset = (key) => {
    setIsTyping(true);
    // Add fake prompt
    setMessages(prev => [...prev, { sender: 'user', text: `Generate the ${presetResponses[key].title}` }]);

    setTimeout(() => {
      setMessages(prev => [...prev, { sender: 'bot', text: `### ${presetResponses[key].title}\n\n${presetResponses[key].content}` }]);
      setIsTyping(false);
    }, 700);
  };

  return (
    <div className="fixed bottom-6 right-6 z-[9999] font-sans">
      {/* Floating Toggle Button */}
      {!isOpen && (
        <button 
          onClick={() => setIsOpen(true)}
          className="flex items-center justify-center w-14 h-14 bg-gradient-to-tr from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-full shadow-2xl transition-all duration-300 transform hover:scale-110 active:scale-95 border border-purple-400/30 group cursor-pointer"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 group-hover:rotate-12 transition-transform duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
          <span className="absolute -top-1 -right-1 flex h-3 w-3">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-pink-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-3 w-3 bg-pink-500"></span>
          </span>
        </button>
      )}

      {/* Expanded Chatbot Panel */}
      {isOpen && (
        <div className="w-[360px] md:w-[400px] h-[520px] premium-glass rounded-2xl flex flex-col border border-purple-500/25 shadow-2xl animate-fade-in overflow-hidden">
          {/* Header */}
          <div className="p-4 bg-gradient-to-r from-purple-900/60 to-indigo-900/60 border-b border-white/5 flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-purple-500 to-indigo-500 flex items-center justify-center text-sm font-bold shadow-md">
                🤖
              </div>
              <div>
                <h3 className="text-sm font-bold text-white leading-tight">AI Career Copilot</h3>
                <span className="text-[11px] text-green-400 flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></span> Active Advisor
                </span>
              </div>
            </div>
            <button 
              onClick={() => setIsOpen(false)}
              className="text-gray-400 hover:text-white transition-colors cursor-pointer"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
              </svg>
            </button>
          </div>

          {/* Messages Panel */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.map((msg, idx) => (
              <div key={idx} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div 
                  className={`max-w-[85%] rounded-2xl p-3.5 text-sm leading-relaxed whitespace-pre-line border relative group ${
                    msg.sender === 'user' 
                      ? 'bg-purple-600/20 text-purple-200 border-purple-500/30 rounded-tr-none' 
                      : 'bg-slate-900/60 text-gray-200 border-white/5 rounded-tl-none pr-8'
                  }`}
                >
                  {/* Speaker Button on Bot Messages */}
                  {msg.sender === 'bot' && (
                    <button 
                      onClick={() => {
                        if (window.speechSynthesis.speaking) {
                          window.speechSynthesis.cancel();
                          return;
                        }
                        const cleanText = msg.text.replace(/[#*`_]/g, '');
                        const utterance = new SpeechSynthesisUtterance(cleanText);
                        window.speechSynthesis.speak(utterance);
                      }}
                      title="Speak advice"
                      className="absolute right-2 top-2 text-gray-500 hover:text-white transition-colors cursor-pointer text-xs"
                    >
                      🔊
                    </button>
                  )}

                  {/* Styled Markdown headers inside chatbot */}
                  {msg.text.split('\n').map((line, lIdx) => {
                    if (line.startsWith('### ')) {
                      return <h4 key={lIdx} className="font-extrabold text-white text-base mt-1 mb-2 glow-text-premium">{line.replace('### ', '')}</h4>;
                    }
                    if (line.startsWith('**') && line.endsWith('**')) {
                      return <strong key={lIdx} className="text-purple-300 block mt-2">{line.replace(/\*\*/g, '')}</strong>;
                    }
                    return <span key={lIdx}>{line}<br /></span>;
                  })}
                </div>
              </div>
            ))}

            {isTyping && (
              <div className="flex justify-start">
                <div className="bg-slate-900/60 border border-white/5 rounded-2xl rounded-tl-none p-4 w-20 flex items-center justify-center h-10">
                  <div className="dot-flashing"></div>
                </div>
              </div>
            )}
            <div ref={chatEndRef} />
          </div>

          {/* Quick Shortcuts */}
          <div className="px-4 py-2 border-t border-white/5 bg-slate-950/20 flex gap-2 overflow-x-auto whitespace-nowrap scrollbar-none select-none">
            <button onClick={() => selectPreset('dsa')} className="inline-block bg-purple-500/10 text-purple-300 text-xs px-2.5 py-1.5 rounded-full border border-purple-500/20 hover:bg-purple-500/20 transition-all cursor-pointer">📘 DSA Cheat Sheet</button>
            <button onClick={() => selectPreset('aiml')} className="inline-block bg-blue-500/10 text-blue-300 text-xs px-2.5 py-1.5 rounded-full border border-blue-500/20 hover:bg-blue-500/20 transition-all cursor-pointer">🧠 AI/ML Prep</button>
            <button onClick={() => selectPreset('react')} className="inline-block bg-cyan-500/10 text-cyan-300 text-xs px-2.5 py-1.5 rounded-full border border-cyan-500/20 hover:bg-cyan-500/20 transition-all cursor-pointer">⚡ React Prep</button>
            <button onClick={() => selectPreset('star')} className="inline-block bg-pink-500/10 text-pink-300 text-xs px-2.5 py-1.5 rounded-full border border-pink-500/20 hover:bg-pink-500/20 transition-all cursor-pointer">💡 STAR Method</button>
            <button onClick={() => selectPreset('fullstack')} className="inline-block bg-orange-500/10 text-orange-300 text-xs px-2.5 py-1.5 rounded-full border border-orange-500/20 hover:bg-orange-500/20 transition-all cursor-pointer">🏗️ System Design</button>
          </div>

          {/* Input Area */}
          <div className="p-3 border-t border-white/5 bg-slate-950/40 flex gap-2">
            <input 
              type="text" 
              value={inputValue}
              onChange={e => setInputValue(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleSend()}
              placeholder="Ask Career AI a question..."
              className="flex-1 bg-slate-900 border border-white/10 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 placeholder-gray-500"
            />
            <button 
              onClick={() => handleSend()}
              className="w-10 h-10 rounded-xl bg-purple-600 hover:bg-purple-700 flex items-center justify-center text-white transition-colors cursor-pointer"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 transform rotate-90" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
