<div class="card-display">
    <div class="card-container">
      <Card>
        <Content style="background: #6200ee; color: white">Employee Management </Content>
        <Actions>
          <Button variant="raised" on:click={() => navigate("employeeList",{replace: true})}>
            <Label>View Employees</Label>
          </Button>
          <Button variant="outlined" on:click={() => navigate("employeeVerify",{replace: true})}>
            <Label>Verify an Employee</Label>
          </Button>
          <Button variant="outlined" on:click={() => navigate("employeeNew",{replace: true})}>
            <Label>Register a New Employee</Label>
          </Button>
        </Actions>
      </Card>
    </div>
  
    <div class="card-container">
      
      <Card>
        <Content style="background: #6200ee; color: white">Vehicle Management </Content>
        <Actions>
          <Button variant="raised" class="mdc-elevation-transition" on:click={() => navigate("vehicle/list",{replace: true})}>
            <Label>View Vehicles</Label>
          </Button>
          <Button variant="outlined" on:click={() => navigate("vehicle/verify",{replace: true})}>
            <Label>Verify a Vehicle</Label>
          </Button>
          <Button variant="outlined" on:click={() => navigate("vehicle/new",{replace: true})}>
            <Label>Register a New Vehicle</Label>
          </Button>
        </Actions>
      </Card>
    </div>
  
    <div class="card-container">
      <Card>
        <Content style="background: #6200ee; color: white">Approval Tracking</Content>
        <Actions fullBleed>
          <Button on:click={() => console.log("Hai")}>
            <Label>Action</Label>
            <i class="material-icons" aria-hidden="true">arrow_forward</i>
          </Button>
        </Actions>
      </Card>
    </div>
  </div>
  <script lang="js">
    import Card, {
      Content,
      PrimaryAction,
      Actions,
      ActionButtons,
      ActionIcons,
    } from '@smui/card';
    import Button, { Label } from '@smui/button';
    import IconButton, { Icon } from '@smui/icon-button';
    import { Router, Route, Link, navigate } from "svelte-navigator";
  </script>
  