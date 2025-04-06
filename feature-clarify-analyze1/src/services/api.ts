import axios from 'axios';
import logger from '@/lib/logger';

// Create an axios instance with the base URL pointing to our backend
// Using the proxy configured in vite.config.ts
const api = axios.create({
  baseURL: '/contextcoach',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Log the base URL for debugging
logger.debug('API base URL configured as:', { baseURL: api.defaults.baseURL });

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    const { method, url, params, data } = config;
    logger.info(`API Request: ${method?.toUpperCase()} ${url}`, { params, data });
    return config;
  },
  (error) => {
    logger.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for logging
api.interceptors.response.use(
  (response) => {
    const { status, config, data } = response;
    logger.info(`API Response: ${status} ${config.method?.toUpperCase()} ${config.url}`, { 
      status, 
      data: data ? (typeof data === 'object' ? 'Object data received' : 'Data received') : 'No data'
    });
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, config, data } = error.response;
      logger.error(`API Error: ${status} ${config.method?.toUpperCase()} ${config.url}`, { 
        status, 
        data 
      });
    } else if (error.request) {
      logger.error('API Error: No response received', { request: error.request });
    } else {
      logger.error('API Error:', error.message);
    }
    return Promise.reject(error);
  }
);

// Requirements API
export const requirementsApi = {
  // Get all requirements
  getAllRequirements: async () => {
    const response = await api.get('/api/requirements');
    return response.data;
  },

  // Get requirement by ID
  getRequirementById: async (id: number) => {
    const response = await api.get(`/api/requirements/${id}`);
    return response.data;
  },

  // Create requirement from text
  createRequirementFromText: async (title: string, content: string) => {
    // Using JSON data instead of FormData
    const data = { title, content };
    const response = await api.post('/api/requirements/text', data);
    return response.data;
  },

  // Upload requirement file
  uploadRequirementFile: async (file: File, title: string) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    const response = await api.post('/api/requirements/upload', formData);
    return response.data;
  },

  // Analyze requirement
  analyzeRequirement: async (id: number) => {
    const response = await api.post(`/api/requirements/${id}/analyze`);
    return response.data;
  },

  // Estimate scope
  estimateScope: async (id: number) => {
    const response = await api.post(`/api/requirements/${id}/estimate`);
    return response.data;
  },

  // Generate implementation plan
  generateImplementationPlan: async (id: number) => {
    const response = await api.post(`/api/requirements/${id}/plan`);
    return response.data;
  },

  // Calculate story points
  calculateStoryPoints: async (id: number, repositoryComplexity?: number) => {
    const url = repositoryComplexity !== undefined 
      ? `/api/requirements/${id}/story-points?repositoryComplexity=${repositoryComplexity}`
      : `/api/requirements/${id}/story-points`;
    const response = await api.post(url);
    return response.data;
  },

  // Calculate story points with developer
  calculateStoryPointsWithDeveloper: async (id: number, developerId: number, repositoryComplexity?: number) => {
    const url = repositoryComplexity !== undefined 
      ? `/api/requirements/${id}/story-points/developer/${developerId}?repositoryComplexity=${repositoryComplexity}`
      : `/api/requirements/${id}/story-points/developer/${developerId}`;
    const response = await api.post(url);
    return response.data;
  }
};

// Developer profiles API
export const developerProfilesApi = {
  // Get all developer profiles
  getAllDeveloperProfiles: async () => {
    const response = await api.get('/api/developers');
    return response.data;
  },

  // Get developer profile by ID
  getDeveloperProfileById: async (id: number) => {
    const response = await api.get(`/api/developers/${id}`);
    return response.data;
  }
};

export default api;
