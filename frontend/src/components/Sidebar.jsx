import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, Briefcase, PlusCircle, UploadCloud, Users, FileCheck } from 'lucide-react';

const Sidebar = () => {
  const { isRecruiter } = useAuth();

  const recruiterLinks = [
    { to: '/recruiter/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/recruiter/jobs', label: 'Manage Jobs', icon: Briefcase },
    { to: '/recruiter/jobs/create', label: 'Post New Job', icon: PlusCircle },
    { to: '/recruiter/upload', label: 'Upload Resumes', icon: UploadCloud },
    { to: '/recruiter/candidates', label: 'All Candidates', icon: Users },
  ];

  const candidateLinks = [
    { to: '/candidate/dashboard', label: 'My Dashboard', icon: LayoutDashboard },
    { to: '/candidate/upload', label: 'Upload Resume', icon: UploadCloud },
    { to: '/candidate/applications', label: 'My Screening Results', icon: FileCheck },
  ];

  const links = isRecruiter ? recruiterLinks : candidateLinks;

  return (
    <aside style={{
      width: '240px',
      backgroundColor: '#ffffff',
      borderRight: '1px solid #e2e8f0',
      padding: '1.5rem 1rem',
      minHeight: 'calc(100vh - 65px)',
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem',
    }}>
      <div style={{ fontSize: '0.75rem', fontWeight: '700', color: '#94a3b8', textTransform: 'uppercase', padding: '0 0.75rem 0.5rem 0.75rem' }}>
        {isRecruiter ? 'Recruiter Portal' : 'Candidate Portal'}
      </div>

      {links.map((link) => {
        const Icon = link.icon;
        return (
          <NavLink
            key={link.to}
            to={link.to}
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '0.625rem 0.875rem',
              borderRadius: '8px',
              fontSize: '0.875rem',
              fontWeight: '500',
              textDecoration: 'none',
              backgroundColor: isActive ? '#eff6ff' : 'transparent',
              color: isActive ? '#2563eb' : '#475569',
            })}
          >
            <Icon size={18} />
            {link.label}
          </NavLink>
        );
      })}
    </aside>
  );
};

export default Sidebar;
