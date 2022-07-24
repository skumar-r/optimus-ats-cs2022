<script lang="js">
// @ts-nocheck

  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import Paper, { Title, Content } from "@smui/paper";
  import Dialog, { Header, Content as DContent, Actions } from "@smui/Dialog";
  import Button from "@smui/button";
  import LinearProgress from '@smui/linear-progress';
  import { onMount, onDestroy } from 'svelte';

  let items = [];
  let actionItem = {};
  let sort = "id";
  let sortDirection = "ascending";
  let open = false;
  let progress = 0;
  let buffer = 99;
  let closed = true;
  let timer;
  let disabled = false;

  onMount(reset);
 
  onDestroy(() => {
    clearInterval(timer);
  });
 
  function reset() {
    progress = 0;
    buffer = 100;
    buffer=
    closed = true;
    clearInterval(timer);
    timer = setInterval(() => {
      progress += 0.01;
 
      if (progress >= 1) {
        progress = 1;
        closed = true;
        clearInterval(timer);
      }
    }, 500);
  }

  if (typeof fetch !== "undefined") {
    fetch(
      "http://localhost:9011//validation/api/workflow/all"
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

  function onAction(action){
    closed = false;
    disabled = true;
    console.log("onAction: "+action);
    fetch("http://localhost:9011//validation/api/workflow/approve", {
        method: "POST",
        headers: [["Content-Type", "application/json"]],
        body: JSON.stringify({
          workflowId: actionItem.id,
          approved: action,
          approvalRemarks: actionItem.approvalRemarks
        }),
      })
      .then((response) => {          
          response.json().then(data=>{
            progress = 1;
            closed = true;
            disabled=false;
            clearInterval(timer);
            if( "INTERNAL_SERVER_ERROR" == data.status){

            }else{
              
            }
          });
          
        })
        .catch((error) => {
          error.json().then(data=>{
            progress = 1;
            closed = true;
            disabled=false;
            clearInterval(timer);            
          });})
  }


</script>

<div>
  <div style="padding: 0;">
    <div class="paper-container">
      <Paper color="primary" variant="outlined" class="mdc-theme--primary">
        <Title>Pending Approval List</Title>        
        <Content>
          <LinearProgress {progress} {closed} {buffer}/>
          <DataTable
            bind:disabled
            sortable
            bind:sort
            bind:sortDirection
            on:SMUIDataTable:sorted={handleSort}
            table$aria-label="Workflow list"
            style="width: 100%;"
          >         
            <Head>
              <Row>                
                <Cell numeric columnId="employeeId">
                  <!-- For numeric columns, icon comes first. -->
                  <Label>Employee ID</Label>
                  <IconButton class="material-icons">arrow_upward</IconButton>
                </Cell>
                <Cell columnId="createdDate" style="width: 100%;">
                  <Label>Created Date</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <IconButton class="material-icons">arrow_upward</IconButton>
                  </Cell>
              </Row>
            </Head>
            <Body>
              {#if items.length > 0}
              {#each items as item (item.id)}
                <Row>
                  <Cell numeric>{item.employeeId}</Cell>
                  <Cell>{item.createdDate}</Cell>
                  <Cell>
                    <Button action="takeAction" on:click={() => (open = true, actionItem= item)}  bind:disabled>
                      <Label>Take Action</Label>
                    </Button>                    
                  </Cell>
                </Row>
              {/each}
              {/if}
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
            <div style="width:33%;float:left;">
              <label for="title">Employee Id</label>      
              <input
                id="employeeId"
                name="employeeId"
                disabled
                bind:value={actionItem.employeeId}
              />
  
              <label for="email">Approval Remarks</label>
              <input
                id="approvalRemarks"
                name="approvalRemarks"
                bind:value={actionItem.approvalRemarks}
              />
            </div>
          </form>
        </DContent>
        <Actions>
            <Button on:click={() => onAction(true)}>
              <Label>Approve</Label>
            </Button>
            <Button on:click={()=>onAction(false)}>
              <Label>Reject</Label>
            </Button>
        </Actions>
      </Dialog>
    </div>
  </div>
</div>
