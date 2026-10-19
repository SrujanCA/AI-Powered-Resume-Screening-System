import React, { useEffect, useState } from 'react';
import { getMyCandidateProfile, getScreeningResultByApplication } from '../../services/api';
import ScoreBadge from '../../components/ScoreBadge';
import { FileCheck, Award } from 'lucide-react';

const MyApplications = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyCandidateProfile()
      .then((res) => {
        if (res.data.success) setProfile(res.data.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1000px', margin: '0 auto' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>My Applications & Screening Results</h1>
        <p style={{ color: '#64748b' }}>View your evaluated compatibility scores and skill feedback</p>
      </div>

      <div className="card" style={{ padding: '2rem', textAlign: 'center' }}>
        <FileCheck size={48} color="#2563eb" style={{ marginBottom: '1rem' }} />
        <h3 style={{ fontSize: '1.25rem', fontWeight: '700', color: '#0f172a' }}>Candidate Screening Status</h3>
        <p style={{ color: '#64748b', marginTop: '0.5rem', maxWidth: '500px', margin: '0.5rem auto 1.5rem auto' }}>
          When recruiters evaluate your uploaded resume against open position requirements, your detailed score breakdown and recommendations will be displayed here.
        </p>
      </div>
    </div>
  );
};

export default MyApplications;
