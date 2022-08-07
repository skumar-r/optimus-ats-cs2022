<script lang="js">
// @ts-nocheck
  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import Paper, { Content } from "@smui/paper";
  import Card, { Actions } from "@smui/card";
  import Button from "@smui/button";
  import Dialog, { Header, Content as DContent } from "@smui/dialog";
  import { Pulse } from "svelte-loading-spinners";
  import IconButton from "@smui/icon-button";
  import EmployeeNew from "./EmployeeNew.svelte";
  import EmployeeVerify from "./EmployeeVerify.svelte";
  import { onMount } from "svelte";

  let items = [];
  let actionItem = {
    empPhoto: "",
  };
  let isRegister = false;
  let isVerify = false;
  let sort = "id";
  let sortDirection = "ascending";
  let open = false;
  let disabled = false;
  let loading = false;
  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";

  onMount(() => {
    loadData();
  });

  function loadData() {
    loading = true;
    if (typeof fetch !== "undefined") {
      fetch("http://localhost:9010/employee", {
        method: "GET",
      })
        .then((response) => {
          response.json().then((data) => {
            loading = false;
            items = data;
          });
        })
        .catch((error) => {
          // Upload failed
          loading = false;
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
  {#if !isRegister && !isVerify}
    <div>
      <div class="card-container">
        <Card>
          <Actions class="buttonContainer">
            <Button
              class="primaryButton"
              variant="outlined"
              on:click={() => {
                isVerify = true;
              }}
            >
              <Label>Verify an Employee</Label>
            </Button>
            <Button
              class="primaryButton"
              variant="outlined"
              on:click={() => {
                isRegister = true;
              }}
            >
              <Label>Register a New Employee</Label>
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
            <span class="pageTitle"
              >Employee List{#if loading == true}
                <Pulse
                  size="60"
                  color="rgb(187,64,74)"
                  unit="px"
                  duration="1s"
                />
              {/if}</span
            >
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
                    <Cell numeric columnId="id">
                      <!-- For numeric columns, icon comes first. -->
                      <Label>ID</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell numeric columnId="csEmployeeId">
                      <!-- For numeric columns, icon comes first. -->
                      <Label>Employee ID</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell columnId="employeeName">
                      <Label>Name</Label>
                      <!-- For non-numeric columns, icon comes second. -->
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell columnId="email">
                      <Label>Email Address</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell columnId="mobile">
                      <Label>Mobile</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell columnId="department">
                      <Label>Department</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell columnId="designation">
                      <Label>Designation</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell><Label>Photo</Label></Cell>
                  </Row>
                </Head>
                <Body>
                  {#each items as item (item.id)}
                    <Row>
                      <Cell numeric class="centered">{item.id}</Cell>
                      <Cell numeric class="centered">{item.csEmployeeId}</Cell>
                      <Cell>{item.employeeName}</Cell>
                      <Cell>{item.email}</Cell>
                      <Cell class="centered">{item.mobile}</Cell>
                      <Cell class="centered">{item.department}</Cell>
                      <Cell class="centered">{item.designation}</Cell>
                      <Cell>
                        <Button
                          action="takeAction"
                          on:click={() => ((open = true), (actionItem = item))}
                          bind:disabled
                        >
                          <Label>Photo</Label>
                        </Button>
                      </Cell>
                    </Row>
                  {/each}
                  {#if items.length == 0}
                    <div style="width: 88%;text-align:center;padding:50px;color:#cf3845;font-size: 20px;">No employee found</div>
                  {/if}
                </Body>
              </DataTable>
            </Content>
          </Paper>
        </div>
      </div>
      <Dialog
        class="emp-dialog"
        bind:open
        fullscreen
        aria-labelledby="fullscreen-title"
        aria-describedby="fullscreen-content"
      >
        <Header>
          <span class="pageTitle" style="padding-top: 20px;">Employee</span>
          <IconButton
            action="close"
            class="material-icons"
            style="margin: 0;
        top: -10px;
        min-width: 20px;
        padding: 15px;
        border-radius:50%;
        width: 20px;
        height: 20px;">close</IconButton
          >
        </Header>
        <DContent id="fullscreen-content">
          <form>
            <!-- svelte-ignore a11y-missing-attribute -->
            <img style="display:block; width:100%;" src={actionItem.empPhoto.length > 50? actionItem.empPhoto : empPhoto} />
          </form>
        </DContent>
      </Dialog>
    </div>
  {/if}
  {#if isRegister}
    <div>
      <div class="card-container">
        <Card>
          <Actions class="buttonContainer">
            <Button
              class="primaryButton"
              variant="outlined"
              on:click={() => {
                isRegister = false;
              }}
            >
              <Label>Back</Label>
            </Button>
          </Actions>
        </Card>
      </div>
      <EmployeeNew bind:isRegister on:message={loadData} />
    </div>
  {/if}
  {#if isVerify}
    <div>
      <div class="card-container">
        <Card>
          <Actions class="buttonContainer">
            <Button
              class="primaryButton"
              variant="outlined"
              on:click={() => {
                isVerify = false;
              }}
            >
              <Label>Back</Label>
            </Button>
          </Actions>
        </Card>
      </div>
      <EmployeeVerify bind:isVerify />
    </div>
  {/if}
</div>
