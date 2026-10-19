import React from 'react';
import { Check, X } from 'lucide-react';

const SkillBadge = ({ skill, matched = true }) => {
  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '0.25rem',
        padding: '0.25rem 0.625rem',
        borderRadius: '6px',
        fontSize: '0.75rem',
        fontWeight: '600',
        backgroundColor: matched ? '#d1fae5' : '#fee2e2',
        color: matched ? '#065f46' : '#991b1b',
        border: `1px solid ${matched ? '#a7f3d0' : '#fca5a5'}`,
        marginRight: '0.375rem',
        marginBottom: '0.375rem',
      }}
    >
      {matched ? <Check size={12} /> : <X size={12} />}
      {skill}
    </span>
  );
};

export default SkillBadge;
