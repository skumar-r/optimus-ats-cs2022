<script lang="js">
  // @ts-nocheck

  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import Paper, { Title, Content } from "@smui/paper";
  import Dialog, { Header, Content as DContent, Actions } from "@smui/Dialog";
  import Button from "@smui/button";
  import LinearProgress from "@smui/linear-progress";
  import { onMount, onDestroy } from "svelte";
  import {
    toasts,
    ToastContainer,
    FlatToast,
  } from "svelte-toasts";
  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let items = [];
  let actionItem = {
    empPhoto: "image",
    comparePhoto: "image",
  };
  let sort = "id";
  let sortDirection = "ascending";
  let open = false;
  let progress = 0;
  let buffer = 99;
  let closed = true;
  let timer;
  let disabled = false;

  let showToast = (message, type) => {
    const toast = toasts.add({
      title: "",
      description: message,
      duration: 5000, // 0 or negative to avoid auto-remove
      placement: "top-right",
      theme: "dark",
      type: type,
      onClick: () => {},
      onRemove: () => {},
    });
  };

  onMount(reset);

  onDestroy(() => {
    clearInterval(timer);
  });

  function reset() {
    progress = 0;
    buffer = 100;
    closed = true;
    clearInterval(timer);
    timer = setInterval(() => {
      progress += 0.01;

      if (progress >= 1) {
        progress = 0;
        closed = true;
        clearInterval(timer);
      }
    }, 500);
  }

  if (typeof fetch !== "undefined") {
    fetch("http://localhost:9011//validation/api/workflow/all")
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

  function onAction(action) {
    closed = false;
    disabled = true;
    console.log("onAction: " + action);
    fetch("http://localhost:9011//validation/api/workflow/approve", {
      method: "POST",
      headers: [["Content-Type", "application/json"]],
      body: JSON.stringify({
        workflowId: actionItem.id,
        approved: action,
        remarks: actionItem.approvalRemarks,
      }),
    })
      .then((response) => {
        response.json().then((data) => {
          progress = 0;
          closed = true;
          disabled = false;
          clearInterval(timer);
          if ("INTERNAL_SERVER_ERROR" == data.status) {
            showToast("Request failed. Please try after some time", "error");
          } else {
            showToast("Success", "success");
          }
        });
      })
      .catch((error) => {
        error.json().then((data) => {
          progress = 0;
          closed = true;
          disabled = false;
          clearInterval(timer);
        });
      });
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
        <Title>Pending Approval List</Title>
        <Content>
          <LinearProgress {progress} {closed} {buffer} />
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
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell columnId="csEmpId">
                  <Label>CS Employee Id</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell columnId="createdDate">
                  <Label>Created Date</Label>
                  <!-- For non-numeric columns, icon comes second. -->
                  <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                </Cell>
                <Cell><Label>Action</Label></Cell>
              </Row>
            </Head>
            <Body>
              {#if items.length > 0}
                {#each items as item (item.id)}
                  <Row>
                    <Cell numeric>{item.employeeId}</Cell>
                    <Cell>{item.csEmpId}</Cell>
                    <Cell>{item.createdDate}</Cell>
                    <Cell>
                      <Button
                        action="takeAction"
                        on:click={() => ((open = true), (actionItem = item))}
                        bind:disabled
                      >
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
            <div style="width:50%;float:left;">
              <label for="title">Employee Id</label>
              <input
                id="employeeId"
                name="employeeId"
                disabled
                bind:value={actionItem.employeeId}
              /><br />
              <label for="title">CS Employee Id</label>
              <input
                id="csEmpId"
                name="csEmpId"
                disabled
                bind:value={actionItem.csEmpId}
              />
              <label for="empPhoto">Employee Photo</label>
              <img
                alt="empPhoto"
                style="display:block; width:200px;"
                src={actionItem.empPhoto && actionItem.empPhoto.length > 50
                  ? actionItem.empPhoto
                  : empPhoto}
              />
              <label for="comparePhoto">Verification Photo</label>
              <img
                alt="comparePhoto"
                style="display:block; width:200px;"
                src={actionItem.comparePhoto &&
                actionItem.comparePhoto.length > 50
                  ? actionItem.comparePhoto
                  : empPhoto}
              />
              <label for="approvalRemarks">Remarks</label>
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
          <Button on:click={() => onAction(false)}>
            <Label>Reject</Label>
          </Button>
        </Actions>
      </Dialog>
      <ToastContainer placement="bottom-right" let:data>
        <FlatToast {data} />
        <!-- Provider template for your toasts -->
      </ToastContainer>
    </div>
  </div>
</div>
