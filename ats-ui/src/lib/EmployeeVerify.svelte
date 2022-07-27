<script>
  import Paper, { Title, Subtitle, Content } from "@smui/paper";
  let empPhoto, idPhoto, empPhotoInput, idPhotoInput;
  let resultAvailable = false;

  let handleSubmit = async (e) => {
    debugger;
    const dataArray = new FormData();
    dataArray.append("type", 'employee');
    dataArray.append("resourceFile", empPhotoInput.files[0]);
    dataArray.append("idCardFile", idPhotoInput.files[0]);
    await fetch("http://localhost:9011/recognition", {
      method: "POST",
      body: dataArray,
    })
            .then((response) => {
              // Successfully uploaded
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
      idPhoto = e.target.result;
    };
  };
</script>

<div class="paper-container">
  {#if !resultAvailable}
    <Paper color="primary" variant="outlined" class="mdc-theme--primary">
      <Title>Verify an Employee</Title>
      <Content>
        <form>
          <label for="employeeImage">Employee Photo</label>
          <img class="avatar" src={empPhoto}  />
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

          <label for="idcardImage">ID Card Photo</label>
          <img class="avatar" src={idPhoto} />
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
          <div style="display: flex;width:100%;justify-content: end;">
            <button type="button" on:click={(e)=>handleSubmit(e)}>Verify</button>
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
