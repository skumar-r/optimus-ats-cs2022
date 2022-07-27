<script lang="js">
  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import Dialog, { Title, Header, Content as DContent, Actions as DActions } from "@smui/Dialog";
  import IconButton from "@smui/icon-button";
  import Paper, { Content } from "@smui/paper";
  import { navigate } from "svelte-navigator";
  import Button from "@smui/button";
  let items = [];
  let open=false;
  let actionItem={};
  let sort = "id";
  let sortDirection = "ascending";
  let serviceName ="all"
  loadData();

 function loadData(){
  if (typeof fetch !== "undefined") {
    fetch("http://localhost:9013/log/"+serviceName, {
      method: "GET",
    })
      .then((response) => response.json())
      .then((json) => (items = json))
      .catch((error) => {
        // Upload failed
      });
  }
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
  <div style="padding: 0;">
    <div class="paper-container">
      <Paper
        color="primary"
        variant="outlined"
        class="mdc-theme--primary no-border"
      >
        <span class="pageTitle">Application Event List</span>
        <Content>
          <DataTable
            sortable
            bind:sort
            bind:sortDirection
            on:SMUIDataTable:sorted={handleSort}
            table$aria-label="Event list"
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
                <Cell columnId="serviceName">
                  <!-- For numeric columns, icon comes first. -->
                  <Label>Service Name</Label>
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell columnId="details">
                  <Label>Action</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell columnId="details">
                  <Label>Message Details</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell columnId="createdDate">
                  <Label>Event Date</Label>
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>                
              </Row>
            </Head>
            <Body >
              {#each items as item (item.id)}
                <Row on:click={() => (open = true, actionItem= item)}>                 
                  <Cell>{item.serviceName}</Cell>
                  <Cell>{item?.action ? item.action : "NO_ACTION"}</Cell>
                  <Cell>{item.details}</Cell>
                  <Cell class="centered">{item.createdDate}</Cell>
                </Row>
              {/each}
            </Body>
          </DataTable>
        </Content>
      </Paper>
      <Dialog
        bind:open
        fullscreen
        aria-labelledby="fullscreen-title"
        aria-describedby="fullscreen-content"
      >
        <Header>
          <Title id="fullscreen-title">Employee Approval</Title>
          <IconButton action="close" class="material-icons">close</IconButton>
        </Header>
        <DContent id="fullscreen-content">         
          <form style="height: 440px;">
            <div style="width:75%;float:left;">
                Service Name : {actionItem.serviceName}     
            
                <label for="email">Action</label>
                <textarea
                  id="action"
                  name="action"
                  disabled
                  cols="20" rows="5"
                  bind:value={actionItem.action}
                />
                <label for="email">Details</label>
                <textarea
                  id="details"
                  name="details"
                  disabled
                  cols="20" rows="5"
                  bind:value={actionItem.details}
                />
            </div>
          </form>
        </DContent>      
    </Dialog>
    </div>
  </div>
</div>
