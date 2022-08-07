<script>
  import Paper, { Title, Content } from "@smui/paper";
  import { toasts, ToastContainer, FlatToast } from "svelte-toasts";
  import Dialog, { Header, Content as DContent, Actions } from "@smui/dialog";
  import Button from "@smui/button";
  import { Label } from "@smui/data-table";
  import IconButton from "@smui/icon-button";
  import CircularProgress from "@smui/circular-progress";
  import { Camera, CameraResultType } from '@capacitor/camera';
  import Card, {
    Content as CardContent,
    PrimaryAction,
    Media,
    MediaContent,
  } from '@smui/card';
  import {navigate} from "svelte-navigator";
 
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
    dataArray.append("resourceFile", empPhotoInput);
    dataArray.append("idCardFile", idPhotoInput);
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
    Camera.getPhoto({
      quality: 90,
      allowEditing: true,
      resultType: CameraResultType.DataUrl
    }).then(image => {
      empPhotoInput = dataURItoBlob(image.dataUrl);
      empPhoto = image.dataUrl;
    }).catch(e=>{
      showToast(e, "error");
    });    
  };

  const onFileSelectedIdPhoto = async (e) => {
   Camera.getPhoto({
      quality: 90,
      allowEditing: true,
      resultType: CameraResultType.DataUrl
    }).then(image => {
       idPhotoInput = dataURItoBlob(image.dataUrl);
       idPhoto=image.dataUrl;
    }).catch(e=>{
      showToast(e, "error");
    });
   
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
      style="margin-top:15px;"
    >
      <span class="pageTitle">Verify an Employee</span>
      <Content>
        <div style="display: flex;width:100%;justify-content: end;">
          <button
          type="button"           
          on:click={() => navigate("/", { replace: true })}           
        >              
          <span style="margin-left: 10px;">Home</span>
        </button>
        </div>
        <form>
          <div style="width:50%;float:left;padding-left:10px;">
            <label for="employeeImage">Employee Photo</label>
            <img class="avatar" src={empPhoto} alt="avatar" on:click={(e) => onFileSelectedEmpPhoto(e)}/>            
            <div
              class="upload"
              on:click={(e) => onFileSelectedEmpPhoto(e)}
            >
            <img
              style="width: 20px;"
              src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={(e) => onFileSelectedEmpPhoto(e)}
            />
              Choose Image
            </div>
            <input
              name="employeeImage"
              id="employeeImage"
              style="display:none"
              type="file"
              accept=".jpg, .jpeg, .png"
              bind:this={empPhotoInput}
            />
          </div><br/>
          <div style="width:50%;float:left;padding-left:10px;">
            <label for="idcardImage">ID Card Photo</label>
            <img class="avatar" src={idPhoto} alt="avatar" on:click={(e) => onFileSelectedIdPhoto(e)}/>            
            <div
              class="upload"
              on:click={(e) => onFileSelectedIdPhoto(e)}
            >
            <img
              style="width: 20px;"
                src="https://static.thenounproject.com/png/625182-200.png"
              alt=""
              on:click={(e) => onFileSelectedIdPhoto(e)}
            />
              Choose Image
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
              <span style="margin-left: 10px;">Verify</span></button>             
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
      <div class="card-display">
        <div class="card-container">
          <Card>
            <Media class="card-media-16x9" aspectRatio="16x9">
              <MediaContent>                
                <img
                style="display:block;width:200px;height:150px;"
                src={actionItem.empPhoto}
                alt="Red dot"
              />
              </MediaContent>
            </Media>
            <CardContent style="color: #888;">
              {#if actionItem.StatusType == "MATCHED"}
              <h2 style="color:green">Status: {actionItem.StatusType}</h2>
              {/if}
              {#if actionItem.StatusType != "MATCHED"}
              <h2 style="color:amber">Status: {actionItem.StatusType}</h2>
              {/if}
              <br/>
              <h3>Employee ID:{actionItem.csEmployeeId}</h3><br/>
              <h3>Employee Name:{actionItem.employeeName}</h3>
            </CardContent>
          </Card>
        </div>
      </div>     
    </DContent>
    <Actions>
      <Button on:click={() => navigate("/", { replace: true })}>
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
    cursor: pointer;
  }
  .avatar {
    display: flex;
    height: 40px;
    width: 40px;
  }
  
</style>
