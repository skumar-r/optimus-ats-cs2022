<script lang="js">
  import { Router, Route } from "svelte-navigator";
  import EmployeeList from "./lib/EmployeeList.svelte";
  import Dashboard from "./lib/Dashboard.svelte";
  import EmployeeNew from "./lib/EmployeeNew.svelte";
  import VehicleList from "./lib/VehicleList.svelte";
  import VehicleNew from "./lib/VehicleNew.svelte";
  import VehicleVerify from "./lib/VehicleVerify.svelte";
  import EmployeeVerify from "./lib/EmployeeVerify.svelte";
  import WorkflowList from "./lib/WorkflowList.svelte";
  import Logs from "./lib/Logs.svelte";
</script>

<Router>
  <Route path="/"><Dashboard /></Route>
  <Route path="employeeList"><EmployeeList /></Route>
  <Route path="employeeNew"><EmployeeNew /></Route>
  <Route path="employeeVerify"><EmployeeVerify /></Route>
  <Route path="vehicle/list"><VehicleList /></Route>
  <Route path="vehicle/new"><VehicleNew /></Route>
  <Route path="vehicle/verify"><VehicleVerify /></Route>
  <Route path="workflow/list"><WorkflowList /></Route>
  <Route path="event/list"><Logs /></Route>
</Router>
