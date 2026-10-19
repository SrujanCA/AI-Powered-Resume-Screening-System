import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getJobById, getCandidateRankings } from '../../services/api';
import ScoreBadge from '../../components/ScoreBadge';
import { Search, Filter, Award, FileText } from 'lucide-react';

const CandidateRankings = () => {
  const { jobId } = useParams();
  const [job, setJob] = useState(null);
  const [rankings, setRankings] = useState([]);
  const [loading, setLoading] = useState(true);

  const [minScore, setMinScore] = useState('');
  const [minExperience, setMinExperience] = useState('');
  const [searchName, setSearchName] = useState('');
  const [skillFilter, setSkillFilter] = useState('');

  useEffect(() => {
    if (jobId) {
      getJobById(jobId).then((res) => {
        if (res.data.success) setJob(res.data.data);
      });
      fetchRankings();
    }
  }, [jobId]);

  const fetchRankings = () => {
    setLoading(true);
    const params = {};
    if (minScore) params.minScore = minScore;
    if (minExperience) params.minExperience = minExperience;
    if (searchName) params.name = searchName;
    if (skillFilter) params.skill = skillFilter;

    getCandidateRankings(jobId, params)
      .then((res) => {
        if (res.data.success) setRankings(res.data.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    fetchRankings();
  };

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>
          Candidate Rankings: <span style={{ color: '#2563eb' }}>{job?.title || 'Job Listing'}</span>
        </h1>
        <p style={{ color: '#64748b' }}>
          Candidates ranked by weighted compatibility score (Skill 40%, Exp 25%, Keywords 15%, Edu 10%, Quals 10%)
        </p>
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <form onSubmit={handleFilterSubmit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', alignItems: 'end' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Search Candidate Name</label>
            <input
              type="text"
              placeholder="Candidate name..."
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              style={{ width: '100%', padding: '0.5rem 0.75rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Min Score (%)</label>
            <input
              type="number"
              placeholder="e.g. 70"
              value={minScore}
              onChange={(e) => setMinScore(e.target.value)}
              style={{ width: '100%', padding: '0.5rem 0.75rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Min Experience (Yrs)</label>
            <input
              type="number"
              placeholder="e.g. 2"
              value={minExperience}
              onChange={(e) => setMinExperience(e.target.value)}
              style={{ width: '100%', padding: '0.5rem 0.75rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
          </div>

          <button type="submit" className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>
            <Filter size={16} /> Filter Results
          </button>
        </form>
      </div>

      {/* Rankings Table */}
      <div className="card" style={{ padding: '0', overflow: 'hidden' }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>Loading ranked candidates...</div>
        ) : rankings.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
            No candidates screened for this position matching the criteria.
          </div>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '1px solid #e2e8f0', color: '#475569', fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                <th style={{ padding: '1rem' }}>Rank</th>
                <th style={{ padding: '1rem' }}>Candidate</th>
                <th style={{ padding: '1rem' }}>Experience</th>
                <th style={{ padding: '1rem' }}>Skill Score (40%)</th>
                <th style={{ padding: '1rem' }}>Overall Score</th>
                <th style={{ padding: '1rem', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {rankings.map((r) => (
                <tr key={r.applicationId} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '1rem', fontWeight: '700', color: '#2563eb' }}>#{r.rank}</td>
                  <td style={{ padding: '1rem' }}>
                    <div style={{ fontWeight: '600', color: '#0f172a' }}>{r.candidateName}</div>
                    <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{r.candidateEmail}</div>
                  </td>
                  <td style={{ padding: '1rem', color: '#334155' }}>{r.totalExperience} Yrs</td>
                  <td style={{ padding: '1rem', fontWeight: '600' }}>{r.skillScore.toFixed(0)}%</td>
                  <td style={{ padding: '1rem' }}>
                    <ScoreBadge score={r.overallScore} />
                  </td>
                  <td style={{ padding: '1rem', textAlign: 'right' }}>
                    <Link to={`/recruiter/report/${r.applicationId}`} className="btn btn-outline" style={{ padding: '0.375rem 0.75rem', fontSize: '0.8rem' }}>
                      <FileText size={14} /> View AI Report
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default CandidateRankings;
