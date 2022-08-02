<script>
  // @ts-nocheck

  import Paper, { Content } from "@smui/paper";
  import { toasts, ToastContainer, FlatToast }  from "svelte-toasts";
  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let idPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let empPhotoInput, idPhotoInput;
  export let isRegister = true;
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
      dataArray.append("photoFrontFile", empPhotoInput.files[0]);
      dataArray.append("photoIDCardFile", idPhotoInput.files[0]);
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
                isRegister = false;
              }
            })
            .catch((error) => {
              // Upload failed
            });
  }
  

  const onFileSelectedEmpPhoto = (e) => {
    let image = e.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(image);
    reader.onload = (e) => {
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

<div>
  <div class="paper-container">
    <Paper
      color="primary"
      variant="outlined"
      class="mdc-theme--primary no-border"
      style="margin-top:25px;">
      <span class="pageTitle">Add a New Employee</span>
      <Content>
        <form style="height: 600px;">
          <div style="width:33%;float:left;">
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
          </div>

          <div style="width:33%;float:left;padding: 0 80px;">
            <label for="employeeImage">Employee Photo</label>
            <img class="avatar" src={empPhoto} alt="d" />
            <img
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
              Choose Image
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
          <div style="width:15%;float:left;">
            <label for="idcardImage">ID Card Photo</label>
            <img class="avatar" src={idPhoto} alt="d" />
            <img
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
              Choose Image
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
            <button type="button" on:click={(e)=>handleSubmit(e)}>Submit</button>
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
