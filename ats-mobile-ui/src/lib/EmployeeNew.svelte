<script>
  // @ts-nocheck

  import Paper, { Content } from "@smui/paper";
  import { toasts, ToastContainer, FlatToast }  from "svelte-toasts";
  import { Camera, CameraResultType } from '@capacitor/camera';
  import {navigate} from "svelte-navigator";

  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let idPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let empPhotoInput, idPhotoInput;

  
  let  csEmployeeId= "",
      employeeName= "",
      email= "",
      mobile= "",
      department= "",
      designation= "";
  let showToast = (message, type) => {
    const toast = toasts.add({
      title: '',
      description: message,
      duration: 5000, // 0 or negative to avoid auto-remove
      placement: 'top-right',
      theme: 'dark',
      type: type,
      onClick: () => {
      },
      onRemove: () => {
      },
    });
  }
  let handleChange = (e)=>{

  }
  let handleSubmit = async (e) => {
    const dataArray = new FormData();
      dataArray.append("csEmployeeId", csEmployeeId);
      dataArray.append("employeeName", employeeName);
      dataArray.append("department", department);
      dataArray.append("designation", designation);
      dataArray.append("email", email);
      dataArray.append("mobile", mobile);
      dataArray.append("hasS3Photo", true);
      dataArray.append("photoFrontFile", empPhotoInput);
      dataArray.append("photoIDCardFile", idPhotoInput);
    await fetch("http://localhost:9010/employee", {
        method: "POST",
        body: dataArray,
      })
            .then((response) => response.json())
            .then((response) => {
              if(!response.success) {
                showToast(response.contentMap.message,"error");
              }
              else{
                navigate("/", { replace: true });
              }
            })
            .catch((error) => {
              // Upload failed
            });
  }
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

    //New Code
    return new Blob([ab], {type: mimeString});

}
</script>

<div>
  <div class="paper-container">
    <Paper
      color="primary"
      variant="outlined"
      class="mdc-theme--primary no-border"
      style="margin-top:25px;">
      <span class="pageTitle">Add a New Employee</span>
      <Content>
        <div style="display: flex;width:100%;justify-content: end;">
          <button
          type="button"           
          on:click={() => navigate("/", { replace: true })}           
        >              
          <span style="margin-left: 10px;">Home</span>
        </button>
        </div>
        <form style="height: 80%">
          <div style="width:100%;float:left;">
            <label for="csEmployeeId">Employee Id</label>
            <input
                    id="csEmployeeId"
                    name="csEmployeeId"
                    on:change={handleChange}
                    bind:value={csEmployeeId}
            />

            <label for="employeeName">Name</label>
            <input
              id="employeeName"
              name="employeeName"
              on:change={handleChange}
              bind:value={employeeName}
            />

            <label for="email">Email Address</label>
            <input
              id="email"
              name="email"
              on:change={handleChange}
              bind:value={email}
            />

            <label for="mobile">Mobile</label>
            <input
              id="mobile"
              name="mobile"
              on:change={handleChange}
              bind:value={mobile}
            />

            <label for="department">Department</label>
            <select
              id="department"
              name="department"
              on:change={handleChange}
              bind:value={department}
            >
              <option />
              <option value="tech">Technology</option>
              <option value="research">Research</option>
              <option value="support">IT Support</option>
              <option value="sales">Sales & Support</option>
              <option value="security">Security</option>
              <option value="admin">Facilities</option>
            </select>

            <label for="designation">Designation</label>
            <select
              id="designation"
              name="designation"
              on:change={handleChange}
              bind:value={designation}
            >
              <option />
              <option value="manager">Manager</option>
              <option value="teammember">Team Member</option>
              <option value="na">N/A</option>
            </select>
            <div>
              <label for="employeeImage">Employee Photo</label>
              <img class="avatar" src={empPhoto} alt="d" on:click={(e) => onFileSelectedEmpPhoto(e)}/>
              <img
                class="upload"
                src="https://static.thenounproject.com/png/625182-200.png"
                alt=""
                on:click={(e) => onFileSelectedEmpPhoto(e)}
              />
              <div
                class="chan"
                on:click={(e) => onFileSelectedEmpPhoto(e)}
              >
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
            </div>
            <div>
              <label for="idcardImage">ID Card Photo</label>
              <img class="avatar" src={idPhoto} alt="d"  on:click={(e) => onFileSelectedIdPhoto(e)}/>
              <img
                class="upload"
                src="https://static.thenounproject.com/png/625182-200.png"
                alt=""
                on:click={(e) => onFileSelectedIdPhoto(e)}
              />
              <div
                class="chan"
                on:click={(e) => onFileSelectedIdPhoto(e)}
              >
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
          </div>
          
          <div style="display: flex;width:100%;justify-content: end;">            
            <button type="button" style="margin-left: 10px;" on:click={(e)=>handleSubmit(e)}><span>Submit</span></button>
          </div>
        </form>
      </Content>
    </Paper>
  </div>
  <ToastContainer placement="bottom-right" let:data={data}>
    <FlatToast {data} /> <!-- Provider template for your toasts -->
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
