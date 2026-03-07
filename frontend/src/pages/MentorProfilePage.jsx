import React from "react"
import MentorHeader from "../components/mentor/MentorHeader"
import MentorSkills from "../components/mentor/MentorSkills"
import MentorAbout from "../components/mentor/MentorAbout"
import MentorReviews from "../components/mentor/MentorReviews"
import BookingWidget from "../components/mentor/BookingWidget"
import GuaranteeCard from "../components/mentor/GuaranteeCard"

export default function MentorProfilePage() {
  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <div className="flex flex-col lg:flex-row gap-8 items-start">
        
        {/* Left Column - Main Profile Content */}
        <div className="flex-1 w-full min-w-0">
          <MentorHeader />
          <MentorSkills />
          <MentorAbout />
          <MentorReviews />
        </div>

        {/* Right Column - Booking Sidebar */}
        <div className="w-full lg:w-[380px] shrink-0 lg:sticky lg:top-24">
          <BookingWidget />
          <GuaranteeCard />
        </div>

      </div>
    </div>
  )
}
