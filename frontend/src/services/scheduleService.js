import api from './api';

const scheduleService = {
  createTimeSlot: async (request) => {
    try {
      const response = await api.post('/mentors/time-slots', request);
      return response;
    } catch (error) {
      console.error('Error creating time slot:', error);
      throw error;
    }
  },

  getMyTimeSlots: async () => {
    try {
      const response = await api.get('/mentors/time-slots');
      return response;
    } catch (error) {
      console.error('Error fetching time slots:', error);
      throw error;
    }
  },

  getMyBookings: async () => {
    try {
      const response = await api.get('/mentors/bookings');
      return response;
    } catch (error) {
      console.error('Error fetching bookings:', error);
      throw error;
    }
  },

  processBooking: async (bookingId, action) => {
    try {
      const response = await api.put(`/mentors/bookings/${bookingId}/status`, { action });
      return response;
    } catch (error) {
      console.error('Error processing booking:', error);
      throw error;
    }
  },

  updateMeetingLink: async (bookingId, meetingLink) => {
    try {
      const response = await api.put(`/mentors/bookings/${bookingId}/meeting-link`, { meetingLink });
      return response;
    } catch (error) {
      console.error('Error updating meeting link:', error);
      throw error;
    }
  }
};

export default scheduleService;
