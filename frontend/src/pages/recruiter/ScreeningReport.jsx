import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getScreeningResultByApplication, getAiReport } from '../../services/api';
import ScoreBadge from '../../components/ScoreBadge';
import SkillBadge from '../../components/SkillBadge';
import { Sparkles, ArrowLeft, CheckCircle2, XCircle, FileText, Award } from 'lucide-react';

const ScreeningReport = () => {
  const { screeningId } = useParams();
  const navigate = useNavigate();

  const [screening, setScreening] = useState(null);
  const [aiReport, setAiReport] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadReport() {
      try {
        const screenRes = await getScreeningResultByApplication(screeningId);
        if (screenRes.data.success) {
          const screenData = screenRes.data.data;
          setScreening(screenData);

          try {
            const aiRes = await getAiReport(screenData.id);
            if (aiRes.data.success) {
              setAiReport(aiRes.data.data);
            }
          } catch (e) {
            console.error('Could not fetch AI report', e);
          }
        }
      } catch (err) {
        console.error('Failed to load screening result', err);
      } finally {
        setLoading(false);
      }
    }

    if (screeningId) loadReport();
  }, [screeningId]);

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '4rem', color: '#64748b' }}>Generating detailed AI Candidate Screening Report...</div>;
  }

  if (!screening) {
    return <div style={{ textAlign: 'center', padding: '4rem', color: '#ef4444' }}>Screening Report Not Found.</div>;
  }

  return (
    <div style={{ padding: '2rem 1.5rem', maxWidth: '1000px', margin: '0 auto' }}>
      <button onClick={() => navigate(-1)} className="btn btn-outline" style={{ marginBottom: '1.5rem', padding: '0.375rem 0.75rem' }}>
        <ArrowLeft size={16} /> Back
      </button>

      {/* Header Banner */}
      <div className="card" style={{ marginBottom: '1.5rem', borderLeft: '6px solid #2563eb' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
          <div>
            <span className="badge badge-primary" style={{ marginBottom: '0.5rem' }}>Candidate Screening Report</span>
            <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>{screening.candidateName}</h1>
            <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
              Applied for <strong>{screening.jobTitle}</strong> at {screening.companyName} &bull; {screening.candidateEmail}
            </p>
          </div>
          <ScoreBadge score={screening.overallScore} size="lg" />
        </div>

        <div style={{ backgroundColor: '#f8fafc', padding: '1rem', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
          <strong style={{ color: '#0f172a' }}>Recruiter Recommendation: </strong>
          <span style={{ color: '#2563eb', fontWeight: '600' }}>{screening.recommendation}</span>
        </div>
      </div>

      {/* Multi-Factor Score Component Breakdown */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.125rem', fontWeight: '700', color: '#0f172a', marginBottom: '1.25rem' }}>
          Multi-Factor Compatibility Score Breakdown
        </h3>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {/* Skill 40% */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.375rem' }}>
              <span>Skills Match (40% Weight)</span>
              <span>{screening.skillScore.toFixed(0)}%</span>
            </div>
            <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${screening.skillScore}%`, height: '100%', backgroundColor: '#2563eb' }} />
            </div>
          </div>

          {/* Experience 25% */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.375rem' }}>
              <span>Experience Match (25% Weight)</span>
              <span>{screening.experienceScore.toFixed(0)}%</span>
            </div>
            <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${screening.experienceScore}%`, height: '100%', backgroundColor: '#10b981' }} />
            </div>
          </div>

          {/* Keywords 15% */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.375rem' }}>
              <span>Keywords & Job Description Overlap (15% Weight)</span>
              <span>{screening.keywordScore.toFixed(0)}%</span>
            </div>
            <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${screening.keywordScore}%`, height: '100%', backgroundColor: '#f59e0b' }} />
            </div>
          </div>

          {/* Education 10% */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.375rem' }}>
              <span>Education Qualification Match (10% Weight)</span>
              <span>{screening.educationScore.toFixed(0)}%</span>
            </div>
            <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${screening.educationScore}%`, height: '100%', backgroundColor: '#8b5cf6' }} />
            </div>
          </div>

          {/* Qualifications 10% */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.375rem' }}>
              <span>Certifications & Additional Qualifications (10% Weight)</span>
              <span>{screening.qualificationScore.toFixed(0)}%</span>
            </div>
            <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${screening.qualificationScore}%`, height: '100%', backgroundColor: '#06b6d4' }} />
            </div>
          </div>
        </div>
      </div>

      {/* Matched & Missing Skills */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '1.5rem' }}>
        <div className="card">
          <h4 style={{ fontSize: '1rem', fontWeight: '700', color: '#065f46', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <CheckCircle2 size={18} /> Matched Skills ({screening.matchedSkills?.length || 0})
          </h4>
          <div>
            {screening.matchedSkills?.length > 0 ? (
              screening.matchedSkills.map((s, idx) => <SkillBadge key={idx} skill={s} matched={true} />)
            ) : (
              <span style={{ fontSize: '0.875rem', color: '#64748b' }}>No direct required skills matched.</span>
            )}
          </div>
        </div>

        <div className="card">
          <h4 style={{ fontSize: '1rem', fontWeight: '700', color: '#991b1b', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <XCircle size={18} /> Missing Skills ({screening.missingSkills?.length || 0})
          </h4>
          <div>
            {screening.missingSkills?.length > 0 ? (
              screening.missingSkills.map((s, idx) => <SkillBadge key={idx} skill={s} matched={false} />)
            ) : (
              <span style={{ fontSize: '0.875rem', color: '#10b981', fontWeight: '600' }}>✓ All required skills matched!</span>
            )}
          </div>
        </div>
      </div>

      {/* AI Semantic Evaluation Report */}
      {aiReport && (
        <div className="card" style={{ backgroundColor: '#ffffff', border: '1px solid #cbd5e1' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem', borderBottom: '1px solid #e2e8f0', pb: '0.75rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#2563eb', fontWeight: '700', fontSize: '1.125rem' }}>
              <Sparkles size={20} /> AI Candidate Evaluation & Reasoning Report
            </div>
            <span className="badge badge-primary">Engine: {aiReport.modelUsed}</span>
          </div>

          <p style={{ color: '#334155', fontSize: '0.95rem', marginBottom: '1.25rem', lineHeight: '1.6' }}>
            {aiReport.overallSummary}
          </p>

          <div style={{ marginBottom: '1.25rem' }}>
            <h5 style={{ fontSize: '0.9rem', fontWeight: '700', color: '#0f172a', marginBottom: '0.5rem' }}>Candidate Strengths</h5>
            <ul style={{ paddingLeft: '1.25rem', color: '#334155', fontSize: '0.875rem' }}>
              {aiReport.detailedStrengths?.map((str, idx) => (
                <li key={idx} style={{ marginBottom: '0.25rem' }}>{str}</li>
              ))}
            </ul>
          </div>

          <div style={{ marginBottom: '1.25rem' }}>
            <h5 style={{ fontSize: '0.9rem', fontWeight: '700', color: '#0f172a', marginBottom: '0.5rem' }}>Areas for Improvement</h5>
            <ul style={{ paddingLeft: '1.25rem', color: '#334155', fontSize: '0.875rem' }}>
              {aiReport.areasForImprovement?.map((area, idx) => (
                <li key={idx} style={{ marginBottom: '0.25rem' }}>{area}</li>
              ))}
            </ul>
          </div>

          <div>
            <h5 style={{ fontSize: '0.9rem', fontWeight: '700', color: '#0f172a', marginBottom: '0.25rem' }}>Experience & Fit Evaluation</h5>
            <p style={{ color: '#475569', fontSize: '0.875rem' }}>{aiReport.experienceEvaluation}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default ScreeningReport;
