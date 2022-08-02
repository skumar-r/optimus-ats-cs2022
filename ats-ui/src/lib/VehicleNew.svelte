<script>
  import { createEventDispatcher } from 'svelte';
  import Paper, { Title, Subtitle, Content } from "@smui/paper";
  import { toasts, ToastContainer, FlatToast, BootstrapToast }  from "svelte-toasts";
  import { useNavigate } from "svelte-navigator";
  const navigate = useNavigate();
  const dispatch = createEventDispatcher();
  let vehiclePhotoInput;
  let regNo = "";
  let vehicleDetails = "";
  let employeeId = "";
  export let isRegister =true;
  let vehiclePhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let employees = [];
  function refreshData() {
    isRegister = false;
    dispatch('message', {});
  }

  if (typeof fetch !== "undefined") {
    fetch("http://localhost:9010/employee", {
      method: "GET",
    })
      .then((response) => response.json())
      .then((json) => (employees = json))
      .catch((error) => {
        // Upload failed
      });
  }

  let handleSubmit = async (e) => {
    const dataArray = new FormData();
    dataArray.append("regNo", regNo);
    dataArray.append("vehicleDetails", vehicleDetails);
    dataArray.append("employeeId", employeeId);
    dataArray.append("hasS3Photo", true);
    dataArray.append("photoFrontFile", vehiclePhotoInput.files[0]);
debugger;
    await fetch("http://localhost:9010/vehicle", {
      method: "POST",
      body: dataArray,
    })
      .then((response) => response.json())
      .then((response) => {
        debugger;
        if (!response.success) {
          showToast(response.contentMap.message, "error");
        } else {
          refreshData();
        }
      })
      .catch((error) => {
        // Upload failed
      });
  }

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

  let handleChange = (e) => {};

  const onFileSelectedIdPhoto = (e) => {
    let image = e.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(image);
    reader.onload = (e) => {
      // @ts-ignore
      vehiclePhoto = e.target.result;
    };
  };
</script>

<div class="paper-container">
  <Paper
    color="primary"
    variant="outlined"
    class="mdc-theme--primary no-border"
    style="margin-top:25px;"
  >
    <span class="pageTitle">Add a New Vehicle</span>
    <Content>
      <form  style="height: 600px;">
        <div style="width:33%;float:left;">
          <label for="regNo">Registration No.</label>
          <input
            id="regNo"
            name="regNo"
            on:change={handleChange}
            bind:value={regNo}
          />

          <label for="vehicleDetails">Employee Id</label>
          <select bind:value={employeeId}>
            {#each employees as value}<option value={value.id}
                >{value.employeeName}</option
              >{/each}
          </select>

          <label for="vehicleDetails">Vehicle Details</label>
          <textarea
            id="vehicleDetails"
            name="vehicleDetails"
            on:change={handleChange}
            bind:value={vehicleDetails}
          />
        </div>
        <div style="width:30%;float:left;padding-left:30px;">
          <label for="vehiclePhoto">Vehicle Photo</label>
          <img class="avatar" src={vehiclePhoto} alt="d" />
          <img
            class="upload"
            src="https://static.thenounproject.com/png/625182-200.png"
            alt=""
            on:click={() => {
              vehiclePhotoInput.click();
            }}
          />
          <div
            class="chan"
            on:click={() => {
              vehiclePhotoInput.click();
            }}
          >
            Choose Image
          </div>
          <input
            name="vehiclePhoto"
            id="vehiclePhoto"
            style="display:none"
            type="file"
            accept=".jpg, .jpeg, .png"
            on:change={(e) => onFileSelectedIdPhoto(e)}
            bind:this={vehiclePhotoInput}
          />
        </div>
        <div style="display: flex;width:100%;justify-content: end;">
          <button type="button" on:click={(e)=>handleSubmit(e)}>Submit</button>
        </div>
      </form>
    </Content>
  </Paper>
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
