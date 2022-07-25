<script lang="js">
  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import Paper, { Title, Content } from "@smui/paper";
  import Card, { Actions } from "@smui/card";
  import { navigate } from "svelte-navigator";
  import Button from "@smui/button";

  let items = [];
  let sort = "id";
  let sortDirection = "ascending";

  if (typeof fetch !== "undefined") {
    fetch("http://localhost:9010/vehicle", {
      method: "GET",
    })
      .then((response) => response.json())
      .then((json) => (items = json))
      .catch((error) => {
        // Upload failed
      });
  }

  function handleSort() {
    items.sort((a, b) => {
      const [aVal, bVal] = [a[sort], b[sort]][
        sortDirection === "ascending" ? "slice" : "reverse"
      ]();
      if (typeof aVal === "string" && typeof bVal === "string") {
        return aVal.localeCompare(bVal);
      }
      return Number(aVal) - Number(bVal);
    });
    items = items;
  }
</script>

<div>
  <div class="card-container">
    <Card>
      <Actions class="buttonContainer">
        <Button
          class="primaryButton"
          variant="outlined"
          on:click={() => navigate("vehicle/verify", { replace: true })}
        >
          <Label>Verify a Vehicle</Label>
        </Button>
        <Button
          class="primaryButton"
          variant="outlined"
          on:click={() => navigate("vehicle/new", { replace: true })}
        >
          <Label>Register a New Vehicle</Label>
        </Button>
      </Actions>
    </Card>
  </div>
  <div style="padding: 0;">
    <div class="paper-container">
      <Paper
        color="primary"
        variant="outlined"
        class="mdc-theme--primary no-border"
      >
        <span class="pageTitle">Employee Vehicle List</span>
        <Content>
          <DataTable
            sortable
            bind:sort
            bind:sortDirection
            on:SMUIDataTable:sorted={handleSort}
            table$aria-label="User list"
            style="width: 100%;"
          >
            <Head>
              <Row>
                <!--
        Note: whatever you supply to "columnId" is
        appended with "-status-label" and used as an ID
        for the hidden label that describes the sort
        status to screen readers.

        You can localize those labels with the
        "sortAscendingAriaLabel" and
        "sortDescendingAriaLabel" props on the DataTable.
      -->
                <Cell numeric columnId="id">
                  <!-- For numeric columns, icon comes first. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                  <Label>Vehicle ID</Label>
                </Cell>
                <Cell columnId="employeeId">
                  <Label>Employee Id</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <!-- <Cell columnId="employeeName">
                  <Label>Employee Name</Label> -->
                <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                <!-- </Cell> -->
                <Cell columnId="regNo">
                  <Label>Register No.</Label>
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <!-- You can turn off sorting for a column. -->
                <Cell columnId="vehicleDetails" style="width:100%;">
                  <Label>Details</Label>
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
              </Row>
            </Head>
            <Body>
              {#each items as item (item.id)}
                <Row>
                  <Cell numeric class="centered">{item.id}</Cell>
                  <Cell class="centered">{item.employeeId}</Cell>
                  <!-- <Cell>{item.employeeName}</Cell> -->
                  <Cell class="centered">{item.regNo}</Cell>
                  <Cell class="centered">{item.vehicleDetails}</Cell>
                </Row>
              {/each}
            </Body>
          </DataTable>
        </Content>
      </Paper>
    </div>
  </div>
</div>
