import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';

import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';

import RecruiterDashboard from './pages/recruiter/RecruiterDashboard';
import JobList from './pages/recruiter/JobList';
import CreateJob from './pages/recruiter/CreateJob';
import UploadResumes from './pages/recruiter/UploadResumes';
import CandidateRankings from './pages/recruiter/CandidateRankings';
import ScreeningReport from './pages/recruiter/ScreeningReport';

import CandidateDashboard from './pages/candidate/CandidateDashboard';
import MyApplications from './pages/candidate/MyApplications';

// Protected Route Component for Authorized Roles
const ProtectedRoute = ({ children, requiredRole }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '4rem', color: '#64748b' }}>Loading application session...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return children;
};

// Layout with Sidebar for Authenticated Users
const DashboardLayout = ({ children }) => {
  return (
    <div style={{ display: 'flex', minHeight: 'calc(100vh - 65px)' }}>
      <Sidebar />
      <main style={{ flex: 1, backgroundColor: '#f8fafc' }}>
        {children}
      </main>
    </div>
  );
};

function AppRoutes() {
  return (
    <div className="app-container">
      <Navbar />
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Home />} />
        <Route path="/jobs" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Recruiter Protected Routes */}
        <Route
          path="/recruiter/dashboard"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <RecruiterDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/jobs"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <JobList />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/jobs/create"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <CreateJob />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/upload"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <UploadResumes />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/candidates"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <CandidateRankings />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/rankings/:jobId"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <CandidateRankings />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter/report/:screeningId"
          element={
            <ProtectedRoute requiredRole="ROLE_RECRUITER">
              <DashboardLayout>
                <ScreeningReport />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />

        {/* Candidate Protected Routes */}
        <Route
          path="/candidate/dashboard"
          element={
            <ProtectedRoute requiredRole="ROLE_CANDIDATE">
              <DashboardLayout>
                <CandidateDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/candidate/upload"
          element={
            <ProtectedRoute requiredRole="ROLE_CANDIDATE">
              <DashboardLayout>
                <UploadResumes />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/candidate/applications"
          element={
            <ProtectedRoute requiredRole="ROLE_CANDIDATE">
              <DashboardLayout>
                <MyApplications />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />

        {/* Fallback Catch-all */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </Router>
  );
}

export default App;
