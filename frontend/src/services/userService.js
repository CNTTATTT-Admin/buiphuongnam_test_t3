import api from './api';

const getMentors = async () => {
  const response = await api.get('/public/mentors');
  return response;
};

const getUserById = async (id) => {
  const response = await api.get(`/public/mentors/${id}`);
  return response;
};

export const userService = {
  getMentors,
  getUserById,
};
