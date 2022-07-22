<script>
  // @ts-nocheck

  import { createForm } from "svelte-forms-lib";
  import Paper, { Title, Subtitle, Content } from "@smui/paper";
  let empPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let idPhoto =
    "https://digitalfinger.id/wp-content/uploads/2019/12/no-image-available-icon-6.png";
  let empPhotoInput, idPhotoInput;

  const { form, handleChange, handleSubmit } = createForm({
    initialValues: {
      title: "",
      name: "",
      email: "",
      department: "",
      empPhoto: "",
    },
    onSubmit: (values) => {
      const dataArray = new FormData();
      dataArray.append("title", values.title);
      dataArray.append("employeeName", values.name);
      dataArray.append("department", values.department);
      dataArray.append("designation", "Manager");
      dataArray.append("email", values.email);
      dataArray.append("mobile", "9876543210");
      dataArray.append("hasS3Photo", false);
      dataArray.append("photoFrontFile", empPhotoInput);
      dataArray.append("photoIdCardFile", idPhotoInput);
      fetch("http://localhost:9010/employee", {
        method: "POST",
        headers: [["Content-Type", "multipart/form-data"]],
        body: dataArray,
      })
        .then((response) => {
          // Successfully uploaded
        })
        .catch((error) => {
          // Upload failed
        });
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
      // @ts-ignore
      idPhoto = e.target.result;
    };
  };
</script>

<div>
  <div class="paper-container">
    <Paper color="primary" variant="outlined" class="mdc-theme--primary" style="margin-top:25px;">
      <Title>Add a New Employee</Title>
      <Content>
        <form on:submit={handleSubmit} style="height: 440px;">
          <div style="width:33%;float:left;">
            <label for="title">Title</label>
            <select
              id="title"
              name="title"
              on:change={handleChange}
              bind:value={$form.title}
            >
              <option />
              <option>Mr.</option>
              <option>Mrs.</option>
              <option>Mx.</option>
            </select>

            <label for="name">Name</label>
            <input
              id="name"
              name="name"
              on:change={handleChange}
              bind:value={$form.name}
            />

            <label for="email">Email Address</label>
            <input
              id="email"
              name="email"
              on:change={handleChange}
              bind:value={$form.email}
            />

            <label for="department">Department</label>
            <select
              id="department"
              name="department"
              on:change={handleChange}
              bind:value={$form.department}
            >
              <option />
              <option>Technology</option>
              <option>Research</option>
              <option>IT Support</option>
              <option>Sales & Support</option>
              <option>Security</option>
              <option>Facilities</option>
            </select>
          </div>

          <div style="width:33%;float:left;padding: 0 5px;">
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
          <div style="width:33%;float:left;">
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
          <div style="display: flex;width:100%;justify-content: center;">
            <button type="submit">Submit</button>
          </div>
        </form>
      </Content>
    </Paper>
  </div>
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
