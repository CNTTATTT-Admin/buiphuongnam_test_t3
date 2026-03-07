import React from "react"
import RevenueChart from "../../components/admin/RevenueChart"
import IssueTable from "../../components/admin/IssueTable"
import StatCards from "../../components/admin/StatCards"

export default function AdminDashboard() {
  return (
    <div className="pb-10 font-sans">
      <RevenueChart />
      <IssueTable />
      <StatCards />
    </div>
  )
}
