import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createJob } from '../../services/api';
import { Plus, X, ArrowLeft } from 'lucide-react';

const CreateJob = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [location, setLocation] = useState('');
  const [description, setDescription] = useState('');
  const [minExperience, setMinExperience] = useState(2);
  const [educationRequirement, setEducationRequirement] = useState("Bachelor's Degree in Computer Science or related field");
  
  const [skillInput, setSkillInput] = useState('');
  const [skills, setSkills] = useState([
    { skillName: 'Java', required: true },
    { skillName: 'Spring Boot', required: true },
    { skillName: 'REST API', required: true },
    { skillName: 'MySQL', required: true },
  ]);

  const [submitting, setSubmitting] = useState(false);

  const addSkill = () => {
    if (skillInput.trim()) {
      setSkills([...skills, { skillName: skillInput.trim(), required: true }]);
      setSkillInput('');
    }
  };

  const removeSkill = (index) => {
    setSkills(skills.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (skills.length === 0) {
      alert('Please add at least one required skill');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        title,
        companyName,
        location,
        description,
        minExperience: parseInt(minExperience),
        educationRequirement,
        skills,
      };

      const res = await createJob(payload);
      if (res.data.success) {
        navigate('/recruiter/jobs');
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create job specification');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '800px', margin: '0 auto' }}>
      <button onClick={() => navigate(-1)} className="btn btn-outline" style={{ marginBottom: '1.5rem', padding: '0.375rem 0.75rem' }}>
        <ArrowLeft size={16} /> Back
      </button>

      <div className="card" style={{ padding: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: '800', color: '#0f172a', marginBottom: '0.25rem' }}>
          Create New Job Specification
        </h1>
        <p style={{ color: '#64748b', fontSize: '0.9rem', marginBottom: '2rem' }}>
          Define the required skills and experience for the screening algorithm to evaluate candidate resumes.
        </p>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
                Job Title *
              </label>
              <input
                type="text"
                required
                placeholder="e.g. Java Backend Developer"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
                Company Name *
              </label>
              <input
                type="text"
                required
                placeholder="e.g. Tech Solutions Inc."
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
                Location
              </label>
              <input
                type="text"
                placeholder="e.g. Remote / New York"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
                Min Experience (Years)
              </label>
              <input
                type="number"
                min="0"
                value={minExperience}
                onChange={(e) => setMinExperience(e.target.value)}
                style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
                Education Requirement
              </label>
              <input
                type="text"
                value={educationRequirement}
                onChange={(e) => setEducationRequirement(e.target.value)}
                style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
              Job Description & Overview *
            </label>
            <textarea
              required
              rows={4}
              placeholder="Provide a detailed description of the role, responsibilities, and key technologies..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1', fontFamily: 'inherit' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '600', color: '#334155', marginBottom: '0.375rem' }}>
              Required Job Skills (40% Weighted Score) *
            </label>
            
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
              <input
                type="text"
                placeholder="Add skill (e.g. Docker, AWS, React)..."
                value={skillInput}
                onChange={(e) => setSkillInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addSkill())}
                style={{ flex: 1, padding: '0.625rem 0.875rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
              />
              <button type="button" onClick={addSkill} className="btn btn-outline">
                <Plus size={16} /> Add Skill
              </button>
            </div>

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {skills.map((s, idx) => (
                <span key={idx} style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.375rem',
                  padding: '0.375rem 0.75rem',
                  backgroundColor: '#dbeafe',
                  color: '#1e40af',
                  borderRadius: '6px',
                  fontWeight: '600',
                  fontSize: '0.875rem',
                }}>
                  {s.skillName}
                  <X size={14} style={{ cursor: 'pointer' }} onClick={() => removeSkill(idx)} />
                </span>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="btn btn-primary"
            style={{ padding: '0.875rem', fontSize: '1rem', marginTop: '1rem' }}
          >
            {submitting ? 'Creating Job...' : 'Save & Publish Job Description'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateJob;
