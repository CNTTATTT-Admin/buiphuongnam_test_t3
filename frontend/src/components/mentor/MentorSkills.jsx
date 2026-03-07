import React from "react"
import { BadgeCheck } from "lucide-react"

export default function MentorSkills() {
  const skills = [
    "Java", "React", "TypeScript", "System Design", "IELTS 8.0", "Tailwind CSS", "Node.js"
  ]

  return (
    <div className="bg-white p-8 rounded-xl border border-slate-100 shadow-sm mb-6">
      <h2 className="text-base font-bold text-slate-800 flex items-center gap-2 mb-4">
        <BadgeCheck className="w-5 h-5 text-[#372660]" />
        Kỹ năng chuyên môn
      </h2>
      <div className="flex flex-wrap gap-2">
        {skills.map((skill, index) => (
          <span 
            key={index} 
            className="px-4 py-1.5 bg-slate-100 text-slate-700 text-sm font-semibold rounded-lg hover:bg-slate-200 transition-colors cursor-default"
          >
            {skill}
          </span>
        ))}
      </div>
    </div>
  )
}
