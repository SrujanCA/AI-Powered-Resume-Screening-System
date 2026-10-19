import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyJobs, getAllCandidates, getCandidateRankings } from '../../services/api';
import { Briefcase, Users, FileCheck, Award, PlusCircle, TrendingUp, Sparkles } from 'lucide-react';
import ScoreBadge from '../../components/ScoreBadge';

const RecruiterDashboard = () => {
  const [stats, setStats] = useState({
    totalJobs: 0,
    totalCandidates: 0,
    screenedCount: 0,
    avgScore: 0,
    topCandidate: null,
  });
  const [recentJobs, setRecentJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboardData() {
      try {
        const [jobsRes, candidatesRes] = await Promise.all([
          getMyJobs(),
          getAllCandidates(),
        ]);

        const jobs = jobsRes.data.data || [];
        const candidates = candidatesRes.data.data || [];

        setRecentJobs(jobs);

        let totalScreened = 0;
        let scoreSum = 0;
        let topCand = null;
        let maxScore = -1;

        for (const job of jobs) {
          try {
            const rankRes = await getCandidateRankings(job.id, {});
            const rankings = rankRes.data.data || [];
            totalScreened += rankings.length;

            rankings.forEach((r) => {
              scoreSum += r.overallScore;
              if (r.overallScore > maxScore) {
                maxScore = r.overallScore;
                topCand = r;
              }
            });
          } catch (e) {
            // Ignore if no rankings yet
          }
        }

        const avg = totalScreened > 0 ? (scoreSum / totalScreened).toFixed(1) : 0;

        setStats({
          totalJobs: jobs.length,
          totalCandidates: candidates.length,
          screenedCount: totalScreened,
          avgScore: avg,
          topCandidate: topCand,
        });
      } catch (err) {
        console.error('Error fetching dashboard statistics', err);
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>Recruiter Dashboard</h1>
          <p style={{ color: '#64748b' }}>Overview of posted jobs, candidate evaluations, and AI screening analytics</p>
        </div>
        <Link to="/recruiter/jobs/create" className="btn btn-primary">
          <PlusCircle size={18} /> Post New Job
        </Link>
      </div>

      {/* Metric Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.25rem', marginBottom: '2.5rem' }}>
        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: '#dbeafe', color: '#2563eb', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Briefcase size={24} />
          </div>
          <div>
            <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: '500' }}>Active Jobs</span>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', color: '#0f172a' }}>{stats.totalJobs}</h2>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: '#d1fae5', color: '#059669', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Users size={24} />
          </div>
          <div>
            <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: '500' }}>Total Candidates</span>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', color: '#0f172a' }}>{stats.totalCandidates}</h2>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: '#fef3c7', color: '#d97706', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <FileCheck size={24} />
          </div>
          <div>
            <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: '500' }}>Resumes Screened</span>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', color: '#0f172a' }}>{stats.screenedCount}</h2>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: '#e0e7ff', color: '#4f46e5', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Award size={24} />
          </div>
          <div>
            <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: '500' }}>Average Match Score</span>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', color: '#0f172a' }}>{stats.avgScore}%</h2>
          </div>
        </div>
      </div>

      {/* Top Candidate Spotlight & Recent Jobs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
        {/* Spotlight */}
        <div className="card" style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', color: '#ffffff' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#60a5fa', fontWeight: '600', fontSize: '0.875rem', marginBottom: '1rem' }}>
            <Sparkles size={18} /> Top Ranked Candidate
          </div>

          {stats.topCandidate ? (
            <div>
              <h3 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '0.25rem' }}>{stats.topCandidate.candidateName}</h3>
              <p style={{ color: '#94a3b8', fontSize: '0.9rem', marginBottom: '1rem' }}>{stats.topCandidate.candidateEmail}</p>

              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
                <ScoreBadge score={stats.topCandidate.overallScore} size="lg" />
                <span style={{ fontSize: '0.875rem', color: '#cbd5e1' }}>Experience: {stats.topCandidate.totalExperience} yrs</span>
              </div>

              <Link to={`/recruiter/rankings/${stats.topCandidate.jobId || recentJobs[0]?.id}`} className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
                View Full Ranking
              </Link>
            </div>
          ) : (
            <div style={{ padding: '2rem 0', color: '#94a3b8' }}>
              No screened candidates yet. Upload candidate resumes to begin ranking!
            </div>
          )}
        </div>

        {/* Quick Action Jobs */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3 style={{ fontSize: '1.125rem', fontWeight: '700', color: '#0f172a' }}>My Active Jobs</h3>
            <Link to="/recruiter/jobs" style={{ fontSize: '0.85rem', fontWeight: '600', color: '#2563eb' }}>
              View All ({recentJobs.length})
            </Link>
          </div>

          {recentJobs.length === 0 ? (
            <p style={{ color: '#64748b', fontSize: '0.9rem', padding: '1rem 0' }}>No active job postings found.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {recentJobs.slice(0, 3).map((job) => (
                <div key={job.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', border: '1px solid #e2e8f0', borderRadius: '8px' }}>
                  <div>
                    <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: '#0f172a' }}>{job.title}</h4>
                    <span style={{ fontSize: '0.75rem', color: '#64748b' }}>{job.skills?.length || 0} Skills Required</span>
                  </div>
                  <Link to={`/recruiter/rankings/${job.id}`} className="btn btn-outline" style={{ padding: '0.375rem 0.75rem', fontSize: '0.8rem' }}>
                    Rankings
                  </Link>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecruiterDashboard;
