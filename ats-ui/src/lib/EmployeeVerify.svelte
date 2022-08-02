<script>
  import Paper, { Title, Subtitle, Content } from "@smui/paper";
  import {
    toasts,
    ToastContainer,
    FlatToast,
    BootstrapToast,
  } from "svelte-toasts";
  import Dialog, { Header, Content as DContent, Actions } from "@smui/Dialog";
  import Button from "@smui/button";
  import DataTable, { Head, Body, Row, Cell, Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import { useNavigate } from "svelte-navigator";
  import CircularProgress from "@smui/circular-progress";
  const navigate = useNavigate();
  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let idPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let empPhotoInput, idPhotoInput;
  let resultAvailable = false;
  let actionItem = {};
  let open = false;
  let inProgress = false;
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
  let handleSubmit = async (e) => {
    const dataArray = new FormData();
    dataArray.append("type", "employee");
    dataArray.append("resourceFile", empPhotoInput.files[0]);
    dataArray.append("idCardFile", idPhotoInput.files[0]);
    inProgress = true;
    await fetch("http://localhost:9011/recognition", {
      method: "POST",
      body: dataArray,
    })
      .then((response) => response.json())
      .then((response) => {
        // Successfully uploaded
        inProgress = false;
        if (!response.success) {
          showToast(response.contentMap.message, "error");
        } else {
          response.contentMap.employee.empPhoto =response.contentMap.empPhoto
          response.contentMap.employee.StatusType =
            response.contentMap.StatusType;
          actionItem = response.contentMap.employee;
          open = true;
        }
      })
      .catch((error) => {
        // Upload failed
      });
  };
  const onFileSelectedEmpPhoto = (e) => {
    let image = e.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(image);
    reader.onload = (e) => {
      // @ts-ignore
      empPhoto = e.target.result;
    };
  };

  const onFileSelectedIdPhoto = (e) => {
    let image = e.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(image);
    reader.onload = (e) => {
      // @ts-ignore
      idPhoto = e.target.result;
    };
  };
</script>

<div class="paper-container">
  {#if !resultAvailable}
    <Paper
      color="primary"
      variant="outlined"
      class="mdc-theme--primary  no-border"
      style="margin-top:25px;"
    >
      <span class="pageTitle">Verify an Employee</span>
      <Content>
        <form>
          <div style="width:40%;float:left;padding-left:20px;">
            <label for="employeeImage">Employee Photo</label>
            <img class="avatar" src={empPhoto} />
            <img
              style="width: 25px;"
              class="upload"
              src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={() => {
                empPhotoInput.click();
              }}
            />
            <div
              class="chan"
              on:click={() => {
                empPhotoInput.click();
              }}
            >
              Choose Employee Photo Image
            </div>
            <input
              name="employeeImage"
              id="employeeImage"
              style="display:none"
              type="file"
              accept=".jpg, .jpeg, .png"
              on:change={(e) => onFileSelectedEmpPhoto(e)}
              bind:this={empPhotoInput}
            />
          </div>
          <div style="width:50%;float:left;padding-left:20px;">
            <label for="idcardImage">ID Card Photo</label>
            <img class="avatar" src={idPhoto} />
            <img
              style="width: 25px;"
              class="upload"
              src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={() => {
                idPhotoInput.click();
              }}
            />
            <div
              class="chan"
              on:click={() => {
                idPhotoInput.click();
              }}
            >
              Choose ID Card Image
            </div>
            <input
              name="idcardImage"
              id="idcardImage"
              style="display:none"
              type="file"
              accept=".jpg, .jpeg, .png"
              on:change={(e) => onFileSelectedIdPhoto(e)}
              bind:this={idPhotoInput}
            />
          </div>
          <div style="display: flex;width:100%;justify-content: end;">
            <button
              type="button"
              disabled={inProgress}
              on:click={(e) => handleSubmit(e)}
              style="display:flex;align-items:center;"
            >
              {#if inProgress}
                <CircularProgress
                  class="my-four-colors"
                  style="height: 32px; width: 32px;"
                  indeterminate
                />
              {/if}
              <span style="margin-left: 10px;">Verify</span></button
            >
          </div>
        </form>
      </Content>
    </Paper>
  {:else if resultAvailable}
    <Paper color="primary" variant="outlined" class="mdc-theme--primary">
      <Title>Verification Result</Title>
      <Content />
    </Paper>
  {/if}
  <Dialog
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
      <form style="height: 250px;">
        <img
          style="display:block; width:100px;height:100px;"
          src={actionItem.empPhoto}
          alt="Red dot"
        />
        <label>Employee ID:{actionItem.csEmployeeId}</label>
        <label>Employee Name:{actionItem.employeeName}</label>
        <label>StatusType:{actionItem.StatusType}</label>
      </form>
    </DContent>
    <Actions>
      <Button on:click={() => navigate("/")}>
        <Label>OK</Label>
      </Button>
    </Actions>
  </Dialog>
  <ToastContainer placement="bottom-right" let:data>
    <FlatToast {data} />
    <!-- Provider template for your toasts -->
  </ToastContainer>
</div>

<style>
  .upload {
    display: flex;
    height: 20px;
    width: 20px;
    cursor: pointer;
  }
  .avatar {
    display: flex;
    height: 80px;
    width: 80px;
  }
</style>
