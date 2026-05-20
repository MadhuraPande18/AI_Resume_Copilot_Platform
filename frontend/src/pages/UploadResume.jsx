import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function UploadResume() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;
    setLoading(true);
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      await api.post('/resumes/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      navigate('/ats');
    } catch (err) {
      console.error(err);
      alert('Failed to upload resume.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto mt-6">
      {/* Header */}
      <div className="mb-8 text-center">
        <span className="bg-cyan-500/10 text-cyan-400 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest border border-cyan-500/20 mb-3 inline-block">
          Optimizer Setup
        </span>
        <h1 className="text-4xl md:text-5xl font-black mb-3">Upload Your Resume</h1>
        <p className="text-gray-400 text-sm max-w-md mx-auto leading-relaxed">
          Provide your latest CV as a standard PDF. Our parser scans for technical keywords, matched parameters, and constructs personalized mock questions.
        </p>
      </div>
      
      {/* Dashed upload panel */}
      <div className="premium-glass p-8 md:p-12 rounded-2xl border border-white/5 hover:border-cyan-500/20 transition-all duration-300 shadow-2xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-cyan-500/5 rounded-full blur-3xl pointer-events-none"></div>

        <form onSubmit={handleUpload} className="flex flex-col items-center">
          <div className="w-16 h-16 rounded-2xl bg-cyan-500/10 flex items-center justify-center text-cyan-400 font-bold mb-6 text-2xl animate-pulse">
            📄
          </div>
          
          <div className="w-full max-w-md border-2 border-dashed border-white/10 hover:border-cyan-500/30 rounded-xl p-6 text-center bg-slate-950/20 transition-colors mb-6 cursor-pointer relative group">
            <input 
              type="file" 
              accept=".pdf" 
              onChange={e => setFile(e.target.files[0])} 
              className="absolute inset-0 opacity-0 w-full h-full cursor-pointer"
            />
            {file ? (
              <div>
                <p className="text-xs text-gray-500 font-bold uppercase mb-1">Selected Document</p>
                <p className="text-sm font-black text-cyan-400 break-all">{file.name}</p>
                <p className="text-[11px] text-gray-500 mt-1">Click or drag another to replace</p>
              </div>
            ) : (
              <div>
                <p className="text-sm font-bold text-gray-300 mb-1 group-hover:text-cyan-400 transition-colors">Select PDF Resume</p>
                <p className="text-xs text-gray-500">Supports standard formats up to 5MB</p>
              </div>
            )}
          </div>

          <button 
            type="submit" 
            disabled={!file || loading}
            className="w-full max-w-xs bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-600 hover:to-blue-700 border border-cyan-400/20 text-white py-3.5 rounded-xl font-bold text-sm transition-all duration-300 disabled:opacity-40 cursor-pointer shadow-lg hover:shadow-cyan-500/10"
          >
            {loading ? 'Reading & Analysing PDF...' : 'Upload & Analyze Resume'}
          </button>
        </form>
      </div>
    </div>
  );
}
