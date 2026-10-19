import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllJobs } from '../services/api';
import { Sparkles, Briefcase, CheckCircle2, ShieldCheck, ArrowRight, Zap, Target } from 'lucide-react';
import SkillBadge from '../components/SkillBadge';

const Home = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllJobs()
      .then((res) => {
        if (res.data.success) {
          setJobs(res.data.data);
        }
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '2rem 1.5rem' }}>
      {/* Hero Section */}
      <section style={{ textAlign: 'center', padding: '3rem 1rem', marginBottom: '3rem' }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '0.5rem',
          backgroundColor: '#eff6ff',
          color: '#2563eb',
          padding: '0.5rem 1.25rem',
          borderRadius: '9999px',
          fontWeight: '600',
          fontSize: '0.875rem',
          marginBottom: '1.5rem',
        }}>
          <Sparkles size={18} /> Enterprise AI Resume Screening Platform
        </div>
        <h1 style={{ fontSize: '3rem', fontWeight: '800', lineHeight: 1.2, color: '#0f172a', marginBottom: '1rem' }}>
          Screen Resumes Faster with <br />
          <span style={{ color: '#2563eb' }}>Explainable AI & Precision Skill Matching</span>
        </h1>
        <p style={{ fontSize: '1.25rem', color: '#64748b', maxWidth: '750px', margin: '0 auto 2.5rem auto' }}>
          Automate candidate evaluation using Apache Tika document parsing, multi-factor compatibility scoring, alias skill normalization, and Spring AI explainable reports.
        </p>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}>
          <Link to="/register" className="btn btn-primary" style={{ padding: '0.875rem 2rem', fontSize: '1rem' }}>
            Get Started <ArrowRight size={18} />
          </Link>
          <Link to="/login" className="btn btn-outline" style={{ padding: '0.875rem 2rem', fontSize: '1rem' }}>
            Recruiter Login
          </Link>
        </div>
      </section>

      {/* Feature Grid */}
      <section style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem', marginBottom: '4rem' }}>
        <div className="card">
          <Zap size={32} color="#2563eb" style={{ marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.5rem' }}>Apache Tika Parsing</h3>
          <p style={{ color: '#64748b', fontSize: '0.925rem' }}>
            Instantly parse PDF and Word DOCX resumes to extract contact information, work history, education, and skills.
          </p>
        </div>

        <div className="card">
          <Target size={32} color="#10b981" style={{ marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.5rem' }}>Multi-Factor Scoring</h3>
          <p style={{ color: '#64748b', fontSize: '0.925rem' }}>
            Transparent weighted formula evaluating Skills (40%), Experience (25%), Keywords (15%), Education (10%), and Certifications (10%).
          </p>
        </div>

        <div className="card">
          <ShieldCheck size={32} color="#06b6d4" style={{ marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.5rem' }}>Explainable AI Reports</h3>
          <p style={{ color: '#64748b', fontSize: '0.925rem' }}>
            Understand why candidates earned specific scores with transparent breakdown metrics and LLM semantic reasoning.
          </p>
        </div>
      </section>

      {/* Featured Jobs Section */}
      <section style={{ marginBottom: '4rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <div>
            <h2 style={{ fontSize: '1.75rem', fontWeight: '700', color: '#0f172a' }}>Active Job Postings</h2>
            <p style={{ color: '#64748b' }}>Explore positions open for candidate application & AI screening</p>
          </div>
          <Link to="/register" style={{ fontWeight: '600', textDecoration: 'none' }}>
            Post a Job &rarr;
          </Link>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>Loading open job listings...</div>
        ) : jobs.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
            <Briefcase size={40} color="#94a3b8" style={{ marginBottom: '1rem' }} />
            <h3>No jobs posted yet</h3>
            <p style={{ color: '#64748b', marginTop: '0.5rem' }}>Register as a Recruiter to post your first position!</p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
            {jobs.slice(0, 6).map((job) => (
              <div key={job.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                    <h3 style={{ fontSize: '1.125rem', fontWeight: '700', color: '#0f172a' }}>{job.title}</h3>
                    <span className="badge badge-primary">{job.minExperience}+ yrs exp</span>
                  </div>
                  <p style={{ fontWeight: '600', color: '#475569', fontSize: '0.9rem', marginBottom: '0.5rem' }}>
                    {job.companyName} &bull; {job.location || 'Remote'}
                  </p>
                  <p style={{ color: '#64748b', fontSize: '0.875rem', marginBottom: '1rem', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                    {job.description}
                  </p>

                  <div style={{ marginBottom: '1rem' }}>
                    {job.skills?.slice(0, 4).map((s, idx) => (
                      <SkillBadge key={idx} skill={s.skillName} matched={true} />
                    ))}
                  </div>
                </div>

                <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{job.educationRequirement || 'Bachelor Degree'}</span>
                  <Link to="/login" className="btn btn-outline" style={{ padding: '0.375rem 0.875rem', fontSize: '0.85rem' }}>
                    Apply & Screen
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default Home;
