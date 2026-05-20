import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import UploadResume from './pages/UploadResume';
import AtsResult from './pages/AtsResult';
import InterviewPractice from './pages/InterviewPractice';
import Chatbot from './components/Chatbot';

const PrivateRoute = ({ children }) => {
  return localStorage.getItem('token') ? children : <Navigate to="/login" />;
};

function App() {
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'space-theme');

  useEffect(() => {
    localStorage.setItem('theme', theme);
  }, [theme]);

  return (
    <Router>
      <div className={`min-h-screen premium-bg text-white font-sans flex flex-col relative ${theme}`}>
        <Navbar theme={theme} setTheme={setTheme} />
        <div className="container mx-auto px-4 py-8 flex-1 z-10">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
            <Route path="/upload" element={<PrivateRoute><UploadResume /></PrivateRoute>} />
            <Route path="/ats" element={<PrivateRoute><AtsResult /></PrivateRoute>} />
            <Route path="/practice" element={<PrivateRoute><InterviewPractice /></PrivateRoute>} />
            <Route path="/" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
        <Chatbot />
      </div>
    </Router>
  );
}
export default App;