<Router>
  <nav>
    <Link to="/">Home</Link>    
  </nav>
  <Route path='/'><Dashboard /></Route>
  <Route path="employeeList"><EmployeeList /></Route>
  <Route path="employeeNew"><EmployeeNew /></Route>
  <Route path="employeeVerify"><EmployeeVerify /></Route>
  <Route path="vehicle/list"><VehicleList /></Route>
  <Route path="vehicle/new"><VehicleNew /></Route>
  <Route path="vehicle/verify"><VehicleVerify /></Route>
</Router>
<script lang="js">  
  import { Router, Route, Link } from "svelte-navigator";
  import EmployeeList from './lib/EmployeeList.svelte';
  import Dashboard from './lib/Dashboard.svelte';
  import EmployeeNew from './lib/EmployeeNew.svelte';
  import VehicleList from './lib/VehicleList.svelte';
  import VehicleNew from './lib/VehicleNew.svelte';
  import VehicleVerify from './lib/VehicleVerify.svelte';
  import EmployeeVerify from './lib/EmployeeVerify.svelte';
</script>
