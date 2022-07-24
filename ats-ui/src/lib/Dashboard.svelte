<script>
  import Tabs from "./Tabs.svelte";
  import EmployeeTab from "./EmployeeList.svelte";
  import VehicleTab from "./VehicleList.svelte";
  import ApprovalTab from "./Approval.svelte";
  import WorkflowList from "./WorkflowList.svelte";

  // List of tab items with labels, values and assigned components
  let items = [
    { label: "Employee Management", value: 1, component: EmployeeTab },
    { label: "Vehicle Management", value: 2, component: VehicleTab },
    { label: "Approvals", value: 3, component: WorkflowList },
  ];
</script>
<Tabs {items} />
