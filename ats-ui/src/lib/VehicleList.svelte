<script lang="js">
  import DataTable, {
    Head,
    Body,
    Row,
    Cell,
    Label,
    SortValue,
  } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import Paper, { Title, Subtitle, Content } from '@smui/paper';

  let items = [];
  let sort = "id";
  let sortDirection = "ascending";

  if (typeof fetch !== "undefined") {
    fetch(
      "https://gist.githubusercontent.com/hperrin/e24a4ebd9afdf2a8c283338ae5160a62/raw/dcbf8e6382db49b0dcab70b22f56b1cc444f26d4/users.json"
    )
      .then((response) => response.json())
      .then((json) => (items = json));
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

<div class="paper-container">
  <Paper color="primary" variant="outlined" class="mdc-theme--primary">
    <Title>Employee Vehicle List</Title>
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
              <IconButton class="material-icons">arrow_upward</IconButton>
              <Label>Vehicle ID</Label>
            </Cell>
            <Cell columnId="employeeId" style="width: 100%;">
              <Label>Employee Id</Label>
              <!-- For non-numeric columns, icon comes second. -->
              <IconButton class="material-icons">arrow_upward</IconButton>
            </Cell>
            <Cell columnId="employeeName">
              <Label>Employee Name</Label>
              <IconButton class="material-icons">arrow_upward</IconButton>
            </Cell>
            <Cell columnId="vehicleBrand" l>
              <Label>Vehicle Brand</Label>
              <IconButton class="material-icons">arrow_upward</IconButton>
            </Cell>
            <!-- You can turn off sorting for a column. -->
            <Cell columnId="vehicleModel" l>
              <Label>Vehicle Model</Label>
              <IconButton class="material-icons">arrow_upward</IconButton>
            </Cell>
          </Row>
        </Head>
        <Body>
          {#each items as item (item.id)}
            <Row>
              <Cell numeric>{item.id}</Cell>
              <Cell>{item.employeeId}</Cell>
              <Cell>{item.employeeName}</Cell>
              <Cell>{item.vehicleBrand}</Cell>
              <Cell>{item.vehicleModel}</Cell>
            </Row>
          {/each}
        </Body>
      </DataTable>
    </Content>
  </Paper>
</div>
