import api from './api';

export const bookingService = {
  createBooking: async (bookingRequest) => {
    try {
      const response = await api.post('/bookings', bookingRequest);
      return response;
    } catch (error) {
      console.error('Error creating booking:', error);
      throw error;
    }
  },

  getMyTraineeBookings: async () => {
    try {
      const response = await api.get('/bookings/my-bookings');
      return response;
    } catch (error) {
      console.error('Error fetching mentee bookings:', error);
      throw error;
    }
  },

  completeBooking: async (bookingId) => {
    const response = await api.put(`/bookings/${bookingId}/complete`);
    return response;
  },

  submitReview: async (reviewData) => {
    const response = await api.post('/reviews', reviewData);
    return response;
  },

  getReviewByBooking: async (bookingId) => {
    const response = await api.get(`/reviews/booking/${bookingId}`);
    return response;
  },
};

