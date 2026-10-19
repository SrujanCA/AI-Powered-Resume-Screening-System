import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyCandidateProfile, getAllJobs } from '../../services/api';
import { UploadCloud, FileText, Briefcase, CheckCircle2, Award } from 'lucide-react';
import SkillBadge from '../../components/SkillBadge';
import FileUploader from '../../components/FileUploader';

const CandidateDashboard = () => {
  const [candidateProfile, setCandidateProfile] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [profileRes, jobsRes] = await Promise.all([
          getMyCandidateProfile().catch(() => ({ data: { data: null } })),
          getAllJobs(),
        ]);

        if (profileRes.data?.data) {
          setCandidateProfile(profileRes.data.data);
        }
        if (jobsRes.data?.data) {
          setJobs(jobsRes.data.data);
        }
      } catch (err) {
        console.error('Error loading candidate dashboard', err);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1100px', margin: '0 auto' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>Candidate Portal</h1>
        <p style={{ color: '#64748b' }}>Manage your resume profile, view matched skills, and check job compatibility</p>
      </div>

      {/* Candidate Profile Summary */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{ fontSize: '1.25rem', fontWeight: '700', color: '#0f172a' }}>My Resume Profile</h3>
          <Link to="/candidate/upload" className="btn btn-outline" style={{ padding: '0.375rem 0.75rem', fontSize: '0.85rem' }}>
            <UploadCloud size={16} /> Update Resume
          </Link>
        </div>

        {candidateProfile ? (
          <div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Full Name</span>
                <p style={{ fontWeight: '600', color: '#0f172a' }}>{candidateProfile.name}</p>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Email</span>
                <p style={{ fontWeight: '600', color: '#0f172a' }}>{candidateProfile.email}</p>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Total Experience</span>
                <p style={{ fontWeight: '600', color: '#0f172a' }}>{candidateProfile.totalExperience} Years</p>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Education</span>
                <p style={{ fontWeight: '600', color: '#0f172a' }}>{candidateProfile.education || 'N/A'}</p>
              </div>
            </div>

            <div>
              <span style={{ fontSize: '0.8rem', color: '#64748b', display: 'block', marginBottom: '0.375rem' }}>Extracted Skills</span>
              {candidateProfile.skills?.map((s, idx) => (
                <SkillBadge key={idx} skill={s} matched={true} />
              ))}
            </div>
          </div>
        ) : (
          <div style={{ padding: '1.5rem', textAlign: 'center', backgroundColor: '#f8fafc', borderRadius: '8px', border: '1px dashed #cbd5e1' }}>
            <FileText size={36} color="#94a3b8" style={{ marginBottom: '0.5rem' }} />
            <p style={{ color: '#64748b', fontSize: '0.9rem', marginBottom: '1rem' }}>
              You haven't uploaded a resume yet. Upload your resume to unlock AI skill matching!
            </p>
            <Link to="/candidate/upload" className="btn btn-primary">
              Upload Resume PDF / DOCX
            </Link>
          </div>
        )}
      </div>

      {/* Recommended Jobs */}
      <div>
        <h2 style={{ fontSize: '1.25rem', fontWeight: '700', color: '#0f172a', marginBottom: '1rem' }}>
          Open Job Opportunities for Screening
        </h2>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.25rem' }}>
          {jobs.map((job) => (
            <div key={job.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '700', color: '#0f172a', marginBottom: '0.25rem' }}>{job.title}</h3>
                <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '0.75rem' }}>{job.companyName} &bull; {job.minExperience}+ yrs exp</p>
                <div style={{ marginBottom: '1rem' }}>
                  {job.skills?.slice(0, 3).map((s, idx) => (
                    <SkillBadge key={idx} skill={s.skillName} matched={true} />
                  ))}
                </div>
              </div>

              <Link to="/candidate/upload" className="btn btn-outline" style={{ textAlign: 'center', justifyContent: 'center' }}>
                Screen My Resume &rarr;
              </Link>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CandidateDashboard;
