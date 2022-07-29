<script>
  import Tabs from "./Tabs.svelte";
  import EmployeeTab from "./EmployeeList.svelte";
  import ApprovalTab from "./Approval.svelte";
  import WorkflowList from "./WorkflowList.svelte";
  import Logs from "./Logs.svelte";

  // List of tab items with labels, values and assigned components
  let items = [
    { label: "Employee Management", value: 1, component: EmployeeTab },
    { label: "Approvals", value: 2, component: WorkflowList },
    { label: "App Events Log", value: 3, component: Logs },
  ];
</script>
<Tabs {items} />
