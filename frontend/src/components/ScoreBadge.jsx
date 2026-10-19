import React from 'react';

const ScoreBadge = ({ score, size = 'md' }) => {
  const numericScore = parseFloat(score) || 0;
  
  let color = '#ef4444'; // Red < 60
  let bg = '#fee2e2';
  
  if (numericScore >= 85) {
    color = '#10b981'; // Green
    bg = '#d1fae5';
  } else if (numericScore >= 70) {
    color = '#2563eb'; // Blue
    bg = '#dbeafe';
  } else if (numericScore >= 55) {
    color = '#f59e0b'; // Amber
    bg = '#fef3c7';
  }

  const padding = size === 'lg' ? '0.5rem 1rem' : '0.25rem 0.75rem';
  const fontSize = size === 'lg' ? '1.125rem' : '0.875rem';

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '0.375rem',
        padding,
        fontSize,
        fontWeight: '700',
        borderRadius: '9999px',
        backgroundColor: bg,
        color: color,
        border: `1px solid ${color}40`,
      }}
    >
      <span
        style={{
          width: '8px',
          height: '8px',
          borderRadius: '50%',
          backgroundColor: color,
        }}
      />
      {numericScore.toFixed(1)}%
    </span>
  );
};

export default ScoreBadge;
