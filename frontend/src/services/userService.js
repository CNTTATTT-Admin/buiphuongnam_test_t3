import api from './api';

const getMentors = async () => {
  const response = await api.get('/public/mentors');
  return response;
};

const getUserById = async (id) => {
  const response = await api.get(`/public/mentors/${id}`);
  return response;
};

const getMentorTimeSlots = async (id) => {
  const response = await api.get(`/public/mentors/${id}/time-slots`);
  return response;
};

export const userService = {
  getMentors,
  getUserById,
  getMentorTimeSlots,
  getMenteeProfile: async (userId) => {
    const response = await api.get(`/users/${userId}`);
    return response;
  },
};
