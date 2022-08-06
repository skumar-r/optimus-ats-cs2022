<script>
  import Tabs from "./Tabs.svelte";
  import EmployeeTab from "./EmployeeList.svelte";
  import VehicleTab from "./VehicleList.svelte";

  // List of tab items with labels, values and assigned components
  let items = [
    { label: "Employee Management", value: 1, component: EmployeeTab },
    { label: "Vehicle Management", value: 2, component: VehicleTab }
  ];
</script>

<Tabs {items} />
