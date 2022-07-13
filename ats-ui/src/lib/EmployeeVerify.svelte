<script>
  import { createForm } from "svelte-forms-lib";
  import Paper, { Title, Subtitle, Content } from "@smui/paper";
  let empPhoto, idPhoto, empPhotoInput, idPhotoInput;
  let resultAvailable = false;
  const { form, handleChange, handleSubmit } = createForm({
    initialValues: {
      empPhoto: undefined,
      idPhoto: undefined,
    },
    onSubmit: (values) => {
      resultAvailable = true;
      alert(JSON.stringify(values));
    },
  });

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
        <form on:submit={handleSubmit}>
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
          <button type="submit">Verify</button>
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
