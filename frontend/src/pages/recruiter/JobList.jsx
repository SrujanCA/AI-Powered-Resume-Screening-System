import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyJobs, deleteJob } from '../../services/api';
import { PlusCircle, Trash2, Award, Briefcase, MapPin } from 'lucide-react';
import SkillBadge from '../../components/SkillBadge';

const JobList = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadJobs();
  }, []);

  const loadJobs = () => {
    getMyJobs()
      .then((res) => {
        if (res.data.success) {
          setJobs(res.data.data);
        }
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this job description?')) {
      try {
        await deleteJob(id);
        loadJobs();
      } catch (err) {
        alert('Failed to delete job');
      }
    }
  };

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>Job Management</h1>
          <p style={{ color: '#64748b' }}>Create, update, and manage job specifications for candidate screening</p>
        </div>
        <Link to="/recruiter/jobs/create" className="btn btn-primary">
          <PlusCircle size={18} /> Create New Job
        </Link>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>Loading job postings...</div>
      ) : jobs.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <Briefcase size={40} color="#94a3b8" style={{ marginBottom: '1rem' }} />
          <h3>No jobs created yet</h3>
          <p style={{ color: '#64748b', marginTop: '0.5rem', marginBottom: '1.5rem' }}>Post a new job description to begin screening candidate resumes.</p>
          <Link to="/recruiter/jobs/create" className="btn btn-primary">
            Create Job Now
          </Link>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {jobs.map((job) => (
            <div key={job.id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: '700', color: '#0f172a' }}>{job.title}</h3>
                  <span className="badge badge-primary">{job.minExperience}+ yrs exp</span>
                </div>
                <div style={{ display: 'flex', gap: '1.5rem', color: '#64748b', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                  <span><strong>Company:</strong> {job.companyName}</span>
                  <span><MapPin size={14} style={{ display: 'inline', verticalAlign: '-2px' }} /> {job.location || 'Remote'}</span>
                  <span><strong>Education:</strong> {job.educationRequirement || 'Bachelor'}</span>
                </div>

                <div>
                  {job.skills?.map((s, idx) => (
                    <SkillBadge key={idx} skill={s.skillName} matched={true} />
                  ))}
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <Link to={`/recruiter/rankings/${job.id}`} className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
                  <Award size={16} /> Screen Candidates
                </Link>
                <button
                  onClick={() => handleDelete(job.id)}
                  style={{
                    backgroundColor: '#fee2e2',
                    color: '#991b1b',
                    border: 'none',
                    padding: '0.5rem',
                    borderRadius: '6px',
                    cursor: 'pointer',
                  }}
                  title="Delete Job"
                >
                  <Trash2 size={18} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default JobList;
