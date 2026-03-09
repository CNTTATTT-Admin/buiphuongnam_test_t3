import api from './api';

const getMentors = async () => {
  const response = await api.get('/users/mentors');
  return response;
};

const getUserById = async (id) => {
  const response = await api.get(`/users/${id}`);
  return response;
};

export const userService = {
  getMentors,
  getUserById,
};
