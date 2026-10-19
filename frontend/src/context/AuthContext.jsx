import React, { createContext, useState, useEffect, useContext } from 'react';
import { getCurrentUser, loginUser, registerUser } from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      getCurrentUser()
        .then((res) => {
          if (res.data.success) {
            setUser(res.data.data);
          } else {
            logout();
          }
        })
        .catch(() => logout())
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await loginUser({ email, password });
    if (res.data.success) {
      const authData = res.data.data;
      localStorage.setItem('token', authData.token);
      setToken(authData.token);
      setUser(authData);
      return authData;
    } else {
      throw new Error(res.data.message || 'Login failed');
    }
  };

  const register = async (userData) => {
    const res = await registerUser(userData);
    if (res.data.success) {
      const authData = res.data.data;
      localStorage.setItem('token', authData.token);
      setToken(authData.token);
      setUser(authData);
      return authData;
    } else {
      throw new Error(res.data.message || 'Registration failed');
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
  };

  const isRecruiter = user?.role === 'ROLE_RECRUITER';
  const isCandidate = user?.role === 'ROLE_CANDIDATE';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        register,
        logout,
        isRecruiter,
        isCandidate,
        isAuthenticated: !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
