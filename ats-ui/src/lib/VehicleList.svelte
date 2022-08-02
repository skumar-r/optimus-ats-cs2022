<script lang="js">
  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import Paper, { Content } from "@smui/paper";
  import Card, { Actions } from "@smui/card";
  import Button from "@smui/button";
  import Dialog, { Header, Content as DContent } from "@smui/Dialog";
  import IconButton from "@smui/icon-button";
  import VehicleNew from "./VehicleNew.svelte";
  import VehicleVerify from "./VehicleVerify.svelte";
  import { Pulse } from 'svelte-loading-spinners'
  import {onMount} from "svelte";
  let isRegister = false;
  let isVerify = false;
  let empPhoto =
          "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let actionItem = {
    vehiclePhoto: "",
  };
  let open = false;
  let disabled = false;
  let items = [];
  let sort = "id";
  let sortDirection = "ascending";
  let loading = false;
  onMount(() => {loadData()});

  function loadData() {
    loading = true;
    if (typeof fetch !== "undefined") {
      fetch("http://localhost:9010/vehicle", {
        method: "GET",
      })
      .then((response) =>{
        response.json().then(data=> {
          loading= false;
          items=data
        })
      })
      .catch((error) => {
        loading= false;
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
              <Label>Verify a Vehicle</Label>
            </Button>
            <Button
              class="primaryButton"
              variant="outlined"
              on:click={() => {
                isRegister = true;
              }}
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
            <span class="pageTitle">Employee Vehicle List{#if loading==true}
              <Pulse size="60" color="rgb(187,64,74)" unit="px" duration="1s"></Pulse>
              {/if}</span>
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
                    <Cell numeric columnId="csEmployeeId">
                      <!-- For numeric columns, icon comes first. -->
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                      <Label>Employee Id</Label>
                    </Cell>
                    <Cell columnId="emplyeeName">
                      <Label>Employee Name</Label>
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
                    <Cell columnId="vehicleDetails">
                      <Label>Details</Label>
                      <!-- <IconButton class="material-icons">arrow_upward</IconButton> -->
                    </Cell>
                    <Cell><Label>Photo</Label></Cell>
                  </Row>
                </Head>
                <Body>
                  {#each items as item (item.id)}
                    <Row>
                      <Cell numeric class="centered">{item.csEmployeeId}</Cell>
                      <Cell class="centered">{item.emplyeeName}</Cell>
                      <!-- <Cell>{item.employeeName}</Cell> -->
                      <Cell class="centered">{item.regNo}</Cell>
                      <Cell class="centered">{item.vehicleDetails}</Cell>
                      <Cell class="centered">
                        <Button style="margin-top: 0;"
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
                  <div style="width: 88%;text-align:center;padding:50px;color:#cf3845;font-size: 20px;">No employee vehicle found</div>
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
            <img
                    style="display:block; width:100%;"
                    src={actionItem.vehiclePhoto.length > 50
                ? actionItem.vehiclePhoto
                : empPhoto}
            />
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
      <VehicleNew bind:isRegister={isRegister} />
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
      <VehicleVerify bind:isVerify={isVerify}/>
    </div>
  {/if}
</div>
