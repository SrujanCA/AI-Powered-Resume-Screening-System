import axios from 'axios';

const API = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to attach Bearer Token to requests
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Auth APIs
export const registerUser = (userData) => API.post('/auth/register', userData);
export const loginUser = (credentials) => API.post('/auth/login', credentials);
export const getCurrentUser = () => API.get('/auth/me');

// Job APIs
export const getAllJobs = () => API.get('/jobs');
export const getJobById = (id) => API.get(`/jobs/${id}`);
export const getMyJobs = () => API.get('/jobs/my-jobs');
export const createJob = (jobData) => API.post('/jobs', jobData);
export const updateJob = (id, jobData) => API.put(`/jobs/${id}`, jobData);
export const deleteJob = (id) => API.delete(`/jobs/${id}`);

// Resume Upload APIs
export const uploadResumeFile = (formData) =>
  API.post('/resumes/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

// Candidate APIs
export const parseResumeText = (text) => API.post('/candidates/parse-text', { text });
export const saveCandidateProfile = (candidateData) => API.post('/candidates', candidateData);
export const getAllCandidates = () => API.get('/candidates');
export const getCandidateById = (id) => API.get(`/candidates/${id}`);
export const getMyCandidateProfile = () => API.get('/candidates/me');
export const deleteCandidate = (id) => API.delete(`/candidates/${id}`);

// Screening & Ranking APIs
export const screenCandidate = (jobId, candidateId) => API.post(`/screening/${jobId}/${candidateId}`);
export const getScreeningResult = (id) => API.get(`/screening/${id}`);
export const getScreeningResultByApplication = (appId) => API.get(`/screening/application/${appId}`);
export const getCandidateRankings = (jobId, params) => API.get(`/screening/jobs/${jobId}/rankings`, { params });
export const getAiReport = (id) => API.get(`/screening/${id}/ai-report`);

// Skill APIs
export const normalizeSkill = (skill) => API.post('/skills/normalize', { skill });
export const extractSkills = (text) => API.post('/skills/extract', { text });
export const compareSkills = (requiredSkills, candidateSkills) => API.post('/skills/compare', { requiredSkills, candidateSkills });

export default API;
