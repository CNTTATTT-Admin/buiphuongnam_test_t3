import React from "react"
import FilterSidebar from "../components/search/FilterSidebar"
import MentorListHeader from "../components/search/MentorListHeader"
import MentorSearchResultCard from "../components/search/MentorSearchResultCard"
import Pagination from "../components/search/Pagination"
import { MOCK_SEARCH_MENTORS } from "../data/mockData"

export default function FindMentorPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex flex-col md:flex-row gap-8 max-w-[1400px] mx-auto">
        
        {/* Left Sidebar - Filters */}
        <FilterSidebar />
        
        {/* Main Search Results Area */}
        <div className="flex-1 min-w-0">
          <MentorListHeader totalResults={124} />
          
          <div className="space-y-6">
            {MOCK_SEARCH_MENTORS.map((mentor) => (
              <MentorSearchResultCard key={mentor.id} mentor={mentor} />
            ))}
          </div>

          <Pagination />
        </div>
        
      </div>
    </div>
  )
}
