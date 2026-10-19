import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyJobs, parseResumeText, saveCandidateProfile, screenCandidate } from '../../services/api';
import FileUploader from '../../components/FileUploader';
import { Sparkles, CheckCircle2, Award, FileText } from 'lucide-react';
import SkillBadge from '../../components/SkillBadge';

const UploadResumes = () => {
  const navigate = useNavigate();
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState('');
  const [jobsLoading, setJobsLoading] = useState(true);
  const [jobsError, setJobsError] = useState('');
  
  const [parsedData, setParsedData] = useState(null);
  const [uploadedFileName, setUploadedFileName] = useState('');
  
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [totalExperience, setTotalExperience] = useState(0);
  const [education, setEducation] = useState('');
  const [skills, setSkills] = useState([]);
  
  const [screening, setScreening] = useState(false);
  const [screeningResult, setScreeningResult] = useState(null);

  useEffect(() => {
    getMyJobs()
      .then((res) => {
        if (res.data.success) {
          const availableJobs = res.data.data || [];
          setJobs(availableJobs);
          setSelectedJobId(availableJobs.length > 0 ? String(availableJobs[0].id) : '');
        } else {
          setJobsError(res.data.message || 'Unable to load your job postings.');
        }
      })
      .catch((err) => {
        setJobsError(err.response?.data?.message || 'Unable to load your job postings. Please log in again.');
      })
      .finally(() => setJobsLoading(false));
  }, []);

  const handleUploadSuccess = async (uploadRes) => {
    setUploadedFileName(uploadRes.fileName);
    try {
      const parseRes = await parseResumeText(uploadRes.extractedText);
      if (parseRes.data.success) {
        const data = parseRes.data.data;
        setParsedData(data);
        setName(data.name || 'Candidate');
        setEmail(data.email || 'candidate@example.com');
        setPhone(data.phone || '');
        setTotalExperience(data.totalExperienceYears || 0);
        setEducation(data.education || '');
        setSkills(data.extractedSkills || []);
      }
    } catch (err) {
      console.error('Text parsing failed', err);
    }
  };

  const handleScreenCandidate = async (e) => {
    e.preventDefault();
    if (!selectedJobId) {
      alert('Please select a job target for screening');
      return;
    }

    setScreening(true);
    try {
      // 1. Save candidate profile
      const candidatePayload = {
        name,
        email,
        phone,
        education,
        totalExperience: parseInt(totalExperience),
        skills,
        resumeFilePath: uploadedFileName,
        resumeText: parsedData?.rawText || '',
      };

      const candRes = await saveCandidateProfile(candidatePayload);
      const savedCandidate = candRes.data.data;

      // 2. Trigger Screening Algorithm
      const screenRes = await screenCandidate(selectedJobId, savedCandidate.id);
      if (screenRes.data.success) {
        setScreeningResult(screenRes.data.data);
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Screening failed');
    } finally {
      setScreening(false);
    }
  };

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '900px', margin: '0 auto' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>Upload & Screen Candidate Resume</h1>
        <p style={{ color: '#64748b' }}>Extract structured data using Apache Tika and run multi-factor AI compatibility scoring</p>
      </div>

      {/* Select Target Job */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <label style={{ display: 'block', fontSize: '0.9rem', fontWeight: '700', color: '#0f172a', marginBottom: '0.5rem' }}>
          Select Target Job Specification for Screening *
        </label>
        <select
          value={selectedJobId}
          onChange={(e) => setSelectedJobId(e.target.value)}
          disabled={jobsLoading || Boolean(jobsError) || jobs.length === 0}
          style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.95rem' }}
        >
          {jobsLoading && <option value="">Loading your job postings...</option>}
          {!jobsLoading && jobsError && <option value="">Unable to load job postings</option>}
          {!jobsLoading && !jobsError && jobs.length === 0 && <option value="">No job postings found. Create a job first.</option>}
          {!jobsLoading && !jobsError && jobs.map((j) => (
            <option key={j.id} value={j.id}>
              {j.title} - {j.companyName} ({j.minExperience}+ yrs exp required)
            </option>
          ))}
        </select>
        {jobsError && (
          <p style={{ color: '#b91c1c', fontSize: '0.85rem', marginTop: '0.5rem' }}>{jobsError}</p>
        )}
        {!jobsLoading && !jobsError && jobs.length === 0 && (
          <p style={{ color: '#64748b', fontSize: '0.85rem', marginTop: '0.5rem' }}>Post a job specification before uploading a resume for screening.</p>
        )}
      </div>

      {/* File Upload Area */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.125rem', fontWeight: '700', marginBottom: '1rem' }}>Resume Document Uploader</h3>
        <FileUploader onUploadSuccess={handleUploadSuccess} />
      </div>

      {/* Parsed Information Verification */}
      {parsedData && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#10b981', fontWeight: '600', marginBottom: '1.25rem' }}>
            <Sparkles size={18} /> Apache Tika Extracted Information
          </div>

          <form onSubmit={handleScreenCandidate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Candidate Name</label>
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} style={{ width: '100%', padding: '0.625rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Email</label>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={{ width: '100%', padding: '0.625rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Phone</label>
                <input type="text" value={phone} onChange={(e) => setPhone(e.target.value)} style={{ width: '100%', padding: '0.625rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Total Experience (Years)</label>
                <input type="number" value={totalExperience} onChange={(e) => setTotalExperience(e.target.value)} style={{ width: '100%', padding: '0.625rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.25rem' }}>Education</label>
              <input type="text" value={education} onChange={(e) => setEducation(e.target.value)} style={{ width: '100%', padding: '0.625rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
            </div>

            <button type="submit" disabled={screening} className="btn btn-primary" style={{ padding: '0.875rem', marginTop: '0.5rem', fontSize: '1rem' }}>
              {screening ? 'Evaluating Resume Against Job Requirements...' : 'Run Compatibility Screening Algorithm'}
            </button>
          </form>
        </div>
      )}

      {/* Screening Results Card */}
      {screeningResult && (
        <div className="card" style={{ backgroundColor: '#eff6ff', border: '1px solid #bfdbfe' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <div>
              <span className="badge badge-success" style={{ marginBottom: '0.5rem' }}>Screening Complete</span>
              <h2 style={{ fontSize: '1.5rem', fontWeight: '800', color: '#0f172a' }}>
                Overall Match Score: {screeningResult.overallScore}%
              </h2>
            </div>
            <button
              onClick={() => navigate(`/recruiter/report/${screeningResult.id}`)}
              className="btn btn-primary"
            >
              View Full AI Report &rarr;
            </button>
          </div>

          <p style={{ color: '#334155', fontWeight: '600', marginBottom: '1rem' }}>{screeningResult.recommendation}</p>

          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: '600' }}>Matched Skills:</span>
            {screeningResult.matchedSkills?.map((s, idx) => (
              <SkillBadge key={idx} skill={s} matched={true} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default UploadResumes;
