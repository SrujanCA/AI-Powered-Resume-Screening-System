import React, { useState, useRef } from 'react';
import { UploadCloud, FileText, CheckCircle2, AlertCircle } from 'lucide-react';
import { uploadResumeFile } from '../services/api';

const FileUploader = ({ onUploadSuccess }) => {
  const [isDragging, setIsDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      processFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileSelect = (e) => {
    if (e.target.files && e.target.files[0]) {
      processFile(e.target.files[0]);
    }
  };

  const processFile = async (file) => {
    setError(null);
    setUploading(true);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await uploadResumeFile(formData);
      if (res.data.success) {
        setUploadedFile(res.data.data);
        if (onUploadSuccess) {
          onUploadSuccess(res.data.data);
        }
      } else {
        setError(res.data.message || 'Upload failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to upload file. Unsupported format or file size too large.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={{ width: '100%' }}>
      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current.click()}
        style={{
          border: `2px dashed ${isDragging ? '#2563eb' : '#cbd5e1'}`,
          borderRadius: '12px',
          padding: '2.5rem 1.5rem',
          textAlign: 'center',
          backgroundColor: isDragging ? '#eff6ff' : '#f8fafc',
          cursor: 'pointer',
          transition: 'all 0.2s ease',
        }}
      >
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileSelect}
          accept=".pdf,.docx,.doc"
          style={{ display: 'none' }}
        />

        <div style={{
          width: '56px',
          height: '56px',
          borderRadius: '50%',
          backgroundColor: '#dbeafe',
          color: '#2563eb',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          margin: '0 auto 1rem auto',
        }}>
          <UploadCloud size={28} />
        </div>

        <h3 style={{ fontSize: '1rem', fontWeight: '600', color: '#0f172a', marginBottom: '0.25rem' }}>
          {uploading ? 'Processing & Extracting Text via Apache Tika...' : 'Click or Drag & Drop Resume Here'}
        </h3>
        <p style={{ fontSize: '0.875rem', color: '#64748b' }}>
          Supports PDF, DOCX and DOC files (Max 10MB)
        </p>
      </div>

      {error && (
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          marginTop: '1rem',
          padding: '0.75rem 1rem',
          backgroundColor: '#fee2e2',
          color: '#991b1b',
          borderRadius: '8px',
          fontSize: '0.875rem',
        }}>
          <AlertCircle size={18} />
          {error}
        </div>
      )}

      {uploadedFile && (
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginTop: '1rem',
          padding: '0.875rem 1rem',
          backgroundColor: '#d1fae5',
          color: '#065f46',
          borderRadius: '8px',
          fontSize: '0.875rem',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <FileText size={18} />
            <span style={{ fontWeight: '600' }}>{uploadedFile.fileName}</span>
            <span style={{ color: '#047857', fontSize: '0.75rem' }}>({(uploadedFile.size / 1024).toFixed(1)} KB)</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontWeight: '600' }}>
            <CheckCircle2 size={16} /> Apache Tika Parsed
          </div>
        </div>
      )}
    </div>
  );
};

export default FileUploader;
