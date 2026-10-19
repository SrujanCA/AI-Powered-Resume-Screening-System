import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sparkles, LogOut, User, Briefcase, FileText } from 'lucide-react';

const Navbar = () => {
  const { user, isAuthenticated, logout, isRecruiter } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0.875rem 2rem',
      backgroundColor: '#ffffff',
      borderBottom: '1px solid #e2e8f0',
      position: 'sticky',
      top: 0,
      zIndex: 1000,
    }}>
      <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', textDecoration: 'none' }}>
        <div style={{
          backgroundColor: '#2563eb',
          color: '#ffffff',
          padding: '0.5rem',
          borderRadius: '8px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}>
          <Sparkles size={20} />
        </div>
        <span style={{ fontSize: '1.25rem', fontWeight: '700', color: '#0f172a' }}>
          Resume<span style={{ color: '#2563eb' }}>Screen</span> AI
        </span>
      </Link>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <Link to="/" style={{ color: '#475569', fontWeight: '500', textDecoration: 'none' }}>
          Home
        </Link>
        <Link to="/jobs" style={{ color: '#475569', fontWeight: '500', textDecoration: 'none' }}>
          Jobs
        </Link>

        {isAuthenticated ? (
          <>
            {isRecruiter ? (
              <Link to="/recruiter/dashboard" className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>
                <Briefcase size={16} /> Recruiter Dashboard
              </Link>
            ) : (
              <Link to="/candidate/dashboard" className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>
                <FileText size={16} /> Candidate Dashboard
              </Link>
            )}

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', borderLeft: '1px solid #e2e8f0', paddingLeft: '1rem' }}>
              <div style={{ display: 'flex', flexDirection: 'column', textAlign: 'right' }}>
                <span style={{ fontSize: '0.875rem', fontWeight: '600', color: '#0f172a' }}>{user?.fullName}</span>
                <span style={{ fontSize: '0.75rem', color: '#64748b' }}>
                  {isRecruiter ? 'Recruiter' : 'Candidate'}
                </span>
              </div>
              <button
                onClick={handleLogout}
                style={{
                  background: 'none',
                  border: 'none',
                  color: '#ef4444',
                  cursor: 'pointer',
                  padding: '0.375rem',
                  borderRadius: '6px',
                }}
                title="Logout"
              >
                <LogOut size={18} />
              </button>
            </div>
          </>
        ) : (
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Link to="/login" className="btn btn-outline" style={{ padding: '0.5rem 1rem' }}>
              Log In
            </Link>
            <Link to="/register" className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>
              Register
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
