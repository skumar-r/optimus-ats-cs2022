<script>
  import Tabs from "./Tabs.svelte";
  import EmployeeTab from "./EmployeeList.svelte";
  import VehicleTab from "./VehicleList.svelte";
  import WorkflowList from "./WorkflowList.svelte";
  import Logs from "./Logs.svelte";

  // List of tab items with labels, values and assigned components
  let items = [
    { label: "Employee Management", value: 1, component: EmployeeTab },
    { label: "Vehicle Management", value: 21, component: VehicleTab },
    { label: "Approvals", value: 3, component: WorkflowList },
    { label: "App Events Log", value: 4, component: Logs },
  ];
</script>

<Tabs {items} />
