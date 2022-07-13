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
      <Title>Verify a Vehicle</Title>
      <Content>
        <form on:submit={handleSubmit}>
          <label for="vehicleImage">Vehicle Photo</label>
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
            name="vehicleImage"
            id="vehicleImage"
            style="display:none"
            type="file"
            accept=".jpg, .jpeg, .png"
            on:change={(e) => onFileSelectedEmpPhoto(e)}
            bind:this={empPhotoInput}
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
