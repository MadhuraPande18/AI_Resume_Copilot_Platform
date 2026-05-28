import { Link, useNavigate, useLocation } from 'react-router-dom';
export default function Navbar({ theme, setTheme }) {
  const navigate = useNavigate();
  const location = useLocation(); // Triggers re-render on route changes
  const token = localStorage.getItem('token');
  
  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const toggleTheme = () => {
    setTheme(prev => prev === 'space-theme' ? 'cyber-theme' : 'space-theme');
  };

  return (
    <nav className="premium-glass bg-opacity-65 sticky top-0 z-50 border-b border-white/5 p-4 backdrop-blur-lg shadow-2xl">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="text-2xl font-black bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 via-purple-400 to-pink-500 tracking-wider hover:opacity-90 transition-all duration-300">
          AI INTERVIEW COPILOT
        </Link>
        <div className="flex items-center space-x-2 md:space-x-4">
          {/* Theme Switcher Button */}
          <button 
            onClick={toggleTheme}
            className="flex items-center gap-2 bg-white/5 hover:bg-white/10 border border-white/10 px-3.5 py-2 rounded-xl text-xs font-bold transition-all duration-300 cursor-pointer text-gray-300 hover:text-white"
          >
            {theme === 'space-theme' ? (
              <>
                <span className="text-cyan-400 font-bold">🌌</span>
                <span className="hidden sm:inline">Space Drift</span>
              </>
            ) : (
              <>
                <span className="text-pink-400 font-bold">🔮</span>
                <span className="hidden sm:inline">Cyber Synth</span>
              </>
            )}
          </button>

          {token ? (
            <>
              <Link to="/dashboard" className="text-gray-300 hover:text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-white/5 transition-all duration-300">
                Dashboard
              </Link>
              <Link to="/upload" className="text-gray-300 hover:text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-white/5 transition-all duration-300">
                Upload Resume
              </Link>
              <Link to="/practice" className="text-gray-300 hover:text-white px-3 py-2 rounded-xl text-sm font-medium hover:bg-white/5 transition-all duration-300">
                Mock Studio
              </Link>
              <button 
                onClick={handleLogout} 
                className="bg-rose-500/10 text-rose-400 hover:bg-rose-500/25 border border-rose-500/30 px-4 py-2 rounded-xl text-sm font-bold transition-all duration-300 cursor-pointer shadow-lg shadow-rose-950/20"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-gray-300 hover:text-white px-4 py-2 rounded-xl text-sm font-medium hover:bg-white/5 transition-all duration-300">
                Login
              </Link>
              <Link 
                to="/register" 
                className="bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 border border-purple-500/20 text-white px-4 py-2 rounded-xl text-sm font-bold transition-all duration-300 cursor-pointer shadow-lg hover:shadow-purple-500/10"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}