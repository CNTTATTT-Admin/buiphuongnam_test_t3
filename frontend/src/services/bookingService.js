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
  }
};
