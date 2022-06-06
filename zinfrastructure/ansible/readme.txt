# install docker & docker compose
ansible-playbook -i inventory amprocessor-deploy.yml --extra-vars "variable_host=ssh-alias-host-to-deploy" --tags install_docker

# задеплоить бота:
ansible-playbook -i inventory amprocessor-deploy.yml --extra-vars "variable_host=ssh-alias-host-to-deploy"