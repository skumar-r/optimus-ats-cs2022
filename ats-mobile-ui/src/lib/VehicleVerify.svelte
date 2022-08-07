<script>
  import Paper, { Title, Content } from "@smui/paper";
  import { toasts, ToastContainer, FlatToast } from "svelte-toasts";
  import Dialog, { Header, Content as DContent, Actions } from "@smui/Dialog";
  import Button from "@smui/button";
  import { Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import CircularProgress from "@smui/circular-progress";
  import { Camera, CameraResultType } from '@capacitor/camera';

  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let idPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let empPhotoInput, idPhotoInput;
  let resultAvailable = false;
  let actionItem = {};
  let open = false;
  let inProgress = false;
  export let isVerify = true;
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
    dataArray.append("type", "vehicle");
    dataArray.append("resourceFile", empPhotoInput);
    dataArray.append("idCardFile", idPhotoInput);
    inProgress = true;
    await fetch("http://localhost:9011/recognition/vehicle", {
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
          response.contentMap.employee.empPhoto = response.contentMap.empPhoto;
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
  const onFileSelectedEmpPhoto = async (e) => {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: true,
      resultType: CameraResultType.DataUrl
    });
    empPhotoInput = dataURItoBlob(image.dataUrl);
    empPhoto = image.dataUrl;
  };

  const onFileSelectedIdPhoto = async (e) => {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: true,
      resultType: CameraResultType.DataUrl
    });
    idPhotoInput = dataURItoBlob(image.dataUrl);
    idPhoto=image.dataUrl;
  };
  function dataURItoBlob(dataURI) {
    // convert base64 to raw binary data held in a string
     var byteString = atob(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to an ArrayBuffer
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    //Old Code
    //write the ArrayBuffer to a blob, and you're done
    //var bb = new BlobBuilder();
    //bb.append(ab);
    //return bb.getBlob(mimeString);

    //New Code
    return new Blob([ab], {type: mimeString});

}
</script>

<div class="paper-container">
  {#if !resultAvailable}
    <Paper
      color="primary"
      variant="outlined"
      class="mdc-theme--primary  no-border"
      style="margin-top:25px;"
    >
      <span class="pageTitle">Verify a Vehicle</span>
      <Content>
        <form>
          <div style="width:100%;float:left;padding-left:20px;">
            <label for="idcardImage">ID Card Photo</label>
            <img class="avatar" src={idPhoto} alt="avatar"  on:click={(e) => onFileSelectedIdPhoto(e)}/>
            <img
              style="width: 25px;"
              class="upload"
              src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={(e) => onFileSelectedIdPhoto(e)}
            />
            <div
              class="chan"
              on:click={(e) => onFileSelectedIdPhoto(e)}
            >
              Choose ID Card Image
            </div>
            <input
              name="idcardImage"
              id="idcardImage"
              style="display:none"
              type="file"
              accept=".jpg, .jpeg, .png"             
              bind:this={idPhotoInput}
            />
          </div>
          <div style="width:100%;float:left;padding-left:20px;">
            <label for="employeeImage">Vehicle Photo</label>
            <img class="avatar" src={empPhoto} alt="avatar"  on:click={(e) => onFileSelectedEmpPhoto(e)}/>
            <img
              style="width: 25px;"
              class="upload"
              src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={(e) => onFileSelectedEmpPhoto(e)}
            />
            <div
              class="chan"
              on:click={(e) => onFileSelectedEmpPhoto(e)}
            >
              Choose Vehicle Image
            </div>
            <input
              name="employeeImage"
              id="employeeImage"
              style="display:none"
              type="file"
              accept=".jpg, .jpeg, .png"
              bind:this={empPhotoInput}
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
      <span class="pageTitle" style="padding-top: 20px;">Vehicle</span>
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
        <span>Employee ID:{actionItem.csEmployeeId}</span>
        <span>Employee Name:{actionItem.employeeName}</span>
        <span>StatusType:{actionItem.StatusType}</span>
      </form>
    </DContent>
    <Actions>
      <Button on:click={() => (isVerify = false)}>
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
